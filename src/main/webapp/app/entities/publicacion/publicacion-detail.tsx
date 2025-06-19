import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './publicacion.reducer';

export const PublicacionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const publicacionEntity = useAppSelector(state => state.publicacion.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="publicacionDetailsHeading">
          <Translate contentKey="miPrimeraApp.publicacion.detail.title">Publicacion</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{publicacionEntity.id}</dd>
          <dt>
            <span id="titulo">
              <Translate contentKey="miPrimeraApp.publicacion.titulo">Titulo</Translate>
            </span>
          </dt>
          <dd>{publicacionEntity.titulo}</dd>
          <dt>
            <span id="contenido">
              <Translate contentKey="miPrimeraApp.publicacion.contenido">Contenido</Translate>
            </span>
          </dt>
          <dd>{publicacionEntity.contenido}</dd>
          <dt>
            <span id="fechaPublicacion">
              <Translate contentKey="miPrimeraApp.publicacion.fechaPublicacion">Fecha Publicacion</Translate>
            </span>
          </dt>
          <dd>
            {publicacionEntity.fechaPublicacion ? (
              <TextFormat value={publicacionEntity.fechaPublicacion} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="miPrimeraApp.publicacion.etiqueta">Etiqueta</Translate>
          </dt>
          <dd>
            {publicacionEntity.etiquetas
              ? publicacionEntity.etiquetas.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.nombre}</a>
                    {publicacionEntity.etiquetas && i === publicacionEntity.etiquetas.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>
            <Translate contentKey="miPrimeraApp.publicacion.autor">Autor</Translate>
          </dt>
          <dd>{publicacionEntity.autor ? publicacionEntity.autor.nombre : ''}</dd>
        </dl>
        <Button tag={Link} to="/publicacion" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/publicacion/${publicacionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PublicacionDetail;
