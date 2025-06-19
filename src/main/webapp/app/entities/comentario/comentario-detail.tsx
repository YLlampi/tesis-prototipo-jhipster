import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './comentario.reducer';

export const ComentarioDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const comentarioEntity = useAppSelector(state => state.comentario.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="comentarioDetailsHeading">
          <Translate contentKey="miPrimeraApp.comentario.detail.title">Comentario</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{comentarioEntity.id}</dd>
          <dt>
            <span id="texto">
              <Translate contentKey="miPrimeraApp.comentario.texto">Texto</Translate>
            </span>
          </dt>
          <dd>{comentarioEntity.texto}</dd>
          <dt>
            <span id="fechaCreacion">
              <Translate contentKey="miPrimeraApp.comentario.fechaCreacion">Fecha Creacion</Translate>
            </span>
          </dt>
          <dd>
            {comentarioEntity.fechaCreacion ? (
              <TextFormat value={comentarioEntity.fechaCreacion} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="miPrimeraApp.comentario.publicacion">Publicacion</Translate>
          </dt>
          <dd>{comentarioEntity.publicacion ? comentarioEntity.publicacion.titulo : ''}</dd>
        </dl>
        <Button tag={Link} to="/comentario" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/comentario/${comentarioEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ComentarioDetail;
