import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './etiqueta.reducer';

export const EtiquetaDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const etiquetaEntity = useAppSelector(state => state.etiqueta.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="etiquetaDetailsHeading">
          <Translate contentKey="miPrimeraApp.etiqueta.detail.title">Etiqueta</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{etiquetaEntity.id}</dd>
          <dt>
            <span id="nombre">
              <Translate contentKey="miPrimeraApp.etiqueta.nombre">Nombre</Translate>
            </span>
          </dt>
          <dd>{etiquetaEntity.nombre}</dd>
          <dt>
            <Translate contentKey="miPrimeraApp.etiqueta.publicacion">Publicacion</Translate>
          </dt>
          <dd>
            {etiquetaEntity.publicacions
              ? etiquetaEntity.publicacions.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {etiquetaEntity.publicacions && i === etiquetaEntity.publicacions.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/etiqueta" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/etiqueta/${etiquetaEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EtiquetaDetail;
