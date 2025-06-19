import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './autor.reducer';

export const AutorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const autorEntity = useAppSelector(state => state.autor.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="autorDetailsHeading">
          <Translate contentKey="miPrimeraApp.autor.detail.title">Autor</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{autorEntity.id}</dd>
          <dt>
            <span id="nombre">
              <Translate contentKey="miPrimeraApp.autor.nombre">Nombre</Translate>
            </span>
          </dt>
          <dd>{autorEntity.nombre}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="miPrimeraApp.autor.email">Email</Translate>
            </span>
          </dt>
          <dd>{autorEntity.email}</dd>
          <dt>
            <Translate contentKey="miPrimeraApp.autor.user">User</Translate>
          </dt>
          <dd>{autorEntity.user ? autorEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/autor" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/autor/${autorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AutorDetail;
