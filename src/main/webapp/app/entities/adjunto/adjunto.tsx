import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, byteSize, getSortState, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './adjunto.reducer';

export const Adjunto = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const adjuntoList = useAppSelector(state => state.adjunto.entities);
  const loading = useAppSelector(state => state.adjunto.loading);

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
      <h2 id="adjunto-heading" data-cy="AdjuntoHeading">
        <Translate contentKey="miPrimeraApp.adjunto.home.title">Adjuntos</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="miPrimeraApp.adjunto.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/adjunto/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="miPrimeraApp.adjunto.home.createLabel">Create new Adjunto</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {adjuntoList && adjuntoList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="miPrimeraApp.adjunto.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('nombreArchivo')}>
                  <Translate contentKey="miPrimeraApp.adjunto.nombreArchivo">Nombre Archivo</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('nombreArchivo')} />
                </th>
                <th className="hand" onClick={sort('archivo')}>
                  <Translate contentKey="miPrimeraApp.adjunto.archivo">Archivo</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('archivo')} />
                </th>
                <th className="hand" onClick={sort('tipoMime')}>
                  <Translate contentKey="miPrimeraApp.adjunto.tipoMime">Tipo Mime</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('tipoMime')} />
                </th>
                <th>
                  <Translate contentKey="miPrimeraApp.adjunto.publicacion">Publicacion</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {adjuntoList.map((adjunto, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/adjunto/${adjunto.id}`} color="link" size="sm">
                      {adjunto.id}
                    </Button>
                  </td>
                  <td>{adjunto.nombreArchivo}</td>
                  <td>
                    {adjunto.archivo ? (
                      <div>
                        {adjunto.archivoContentType ? (
                          <a onClick={openFile(adjunto.archivoContentType, adjunto.archivo)}>
                            <img src={`data:${adjunto.archivoContentType};base64,${adjunto.archivo}`} style={{ maxHeight: '30px' }} />
                            &nbsp;
                          </a>
                        ) : null}
                        <span>
                          {adjunto.archivoContentType}, {byteSize(adjunto.archivo)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>{adjunto.tipoMime}</td>
                  <td>
                    {adjunto.publicacion ? <Link to={`/publicacion/${adjunto.publicacion.id}`}>{adjunto.publicacion.titulo}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/adjunto/${adjunto.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/adjunto/${adjunto.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/adjunto/${adjunto.id}/delete`)}
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
              <Translate contentKey="miPrimeraApp.adjunto.home.notFound">No Adjuntos found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Adjunto;
