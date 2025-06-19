import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './comentario.reducer';

export const Comentario = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const comentarioList = useAppSelector(state => state.comentario.entities);
  const loading = useAppSelector(state => state.comentario.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="comentario-heading" data-cy="ComentarioHeading">
        <Translate contentKey="miPrimeraApp.comentario.home.title">Comentarios</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="miPrimeraApp.comentario.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/comentario/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="miPrimeraApp.comentario.home.createLabel">Create new Comentario</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {comentarioList && comentarioList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="miPrimeraApp.comentario.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('texto')}>
                  <Translate contentKey="miPrimeraApp.comentario.texto">Texto</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('texto')} />
                </th>
                <th className="hand" onClick={sort('fechaCreacion')}>
                  <Translate contentKey="miPrimeraApp.comentario.fechaCreacion">Fecha Creacion</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fechaCreacion')} />
                </th>
                <th>
                  <Translate contentKey="miPrimeraApp.comentario.publicacion">Publicacion</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {comentarioList.map((comentario, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/comentario/${comentario.id}`} color="link" size="sm">
                      {comentario.id}
                    </Button>
                  </td>
                  <td>{comentario.texto}</td>
                  <td>
                    {comentario.fechaCreacion ? <TextFormat type="date" value={comentario.fechaCreacion} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {comentario.publicacion ? (
                      <Link to={`/publicacion/${comentario.publicacion.id}`}>{comentario.publicacion.titulo}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/comentario/${comentario.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/comentario/${comentario.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/comentario/${comentario.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="miPrimeraApp.comentario.home.notFound">No Comentarios found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Comentario;
