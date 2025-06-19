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

import { getEntities } from './publicacion.reducer';

export const Publicacion = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const publicacionList = useAppSelector(state => state.publicacion.entities);
  const loading = useAppSelector(state => state.publicacion.loading);

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
      <h2 id="publicacion-heading" data-cy="PublicacionHeading">
        <Translate contentKey="miPrimeraApp.publicacion.home.title">Publicacions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="miPrimeraApp.publicacion.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/publicacion/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="miPrimeraApp.publicacion.home.createLabel">Create new Publicacion</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {publicacionList && publicacionList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="miPrimeraApp.publicacion.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('titulo')}>
                  <Translate contentKey="miPrimeraApp.publicacion.titulo">Titulo</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('titulo')} />
                </th>
                <th className="hand" onClick={sort('contenido')}>
                  <Translate contentKey="miPrimeraApp.publicacion.contenido">Contenido</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contenido')} />
                </th>
                <th className="hand" onClick={sort('fechaPublicacion')}>
                  <Translate contentKey="miPrimeraApp.publicacion.fechaPublicacion">Fecha Publicacion</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fechaPublicacion')} />
                </th>
                <th>
                  <Translate contentKey="miPrimeraApp.publicacion.etiqueta">Etiqueta</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="miPrimeraApp.publicacion.autor">Autor</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {publicacionList.map((publicacion, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/publicacion/${publicacion.id}`} color="link" size="sm">
                      {publicacion.id}
                    </Button>
                  </td>
                  <td>{publicacion.titulo}</td>
                  <td>{publicacion.contenido}</td>
                  <td>
                    {publicacion.fechaPublicacion ? (
                      <TextFormat type="date" value={publicacion.fechaPublicacion} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {publicacion.etiquetas
                      ? publicacion.etiquetas.map((val, j) => (
                          <span key={j}>
                            <Link to={`/etiqueta/${val.id}`}>{val.nombre}</Link>
                            {j === publicacion.etiquetas.length - 1 ? '' : ', '}
                          </span>
                        ))
                      : null}
                  </td>
                  <td>{publicacion.autor ? <Link to={`/autor/${publicacion.autor.id}`}>{publicacion.autor.nombre}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/publicacion/${publicacion.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/publicacion/${publicacion.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/publicacion/${publicacion.id}/delete`)}
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
              <Translate contentKey="miPrimeraApp.publicacion.home.notFound">No Publicacions found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Publicacion;
