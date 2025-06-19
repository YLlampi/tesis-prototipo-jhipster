import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, byteSize, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './adjunto.reducer';

export const AdjuntoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const adjuntoEntity = useAppSelector(state => state.adjunto.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="adjuntoDetailsHeading">
          <Translate contentKey="miPrimeraApp.adjunto.detail.title">Adjunto</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{adjuntoEntity.id}</dd>
          <dt>
            <span id="nombreArchivo">
              <Translate contentKey="miPrimeraApp.adjunto.nombreArchivo">Nombre Archivo</Translate>
            </span>
          </dt>
          <dd>{adjuntoEntity.nombreArchivo}</dd>
          <dt>
            <span id="archivo">
              <Translate contentKey="miPrimeraApp.adjunto.archivo">Archivo</Translate>
            </span>
          </dt>
          <dd>
            {adjuntoEntity.archivo ? (
              <div>
                {adjuntoEntity.archivoContentType ? (
                  <a onClick={openFile(adjuntoEntity.archivoContentType, adjuntoEntity.archivo)}>
                    <img src={`data:${adjuntoEntity.archivoContentType};base64,${adjuntoEntity.archivo}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {adjuntoEntity.archivoContentType}, {byteSize(adjuntoEntity.archivo)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="tipoMime">
              <Translate contentKey="miPrimeraApp.adjunto.tipoMime">Tipo Mime</Translate>
            </span>
          </dt>
          <dd>{adjuntoEntity.tipoMime}</dd>
          <dt>
            <Translate contentKey="miPrimeraApp.adjunto.publicacion">Publicacion</Translate>
          </dt>
          <dd>{adjuntoEntity.publicacion ? adjuntoEntity.publicacion.titulo : ''}</dd>
        </dl>
        <Button tag={Link} to="/adjunto" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/adjunto/${adjuntoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AdjuntoDetail;
