import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getPublicacions } from 'app/entities/publicacion/publicacion.reducer';
import { createEntity, getEntity, reset, updateEntity } from './comentario.reducer';

export const ComentarioUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const publicacions = useAppSelector(state => state.publicacion.entities);
  const comentarioEntity = useAppSelector(state => state.comentario.entity);
  const loading = useAppSelector(state => state.comentario.loading);
  const updating = useAppSelector(state => state.comentario.updating);
  const updateSuccess = useAppSelector(state => state.comentario.updateSuccess);

  const handleClose = () => {
    navigate('/comentario');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPublicacions({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.fechaCreacion = convertDateTimeToServer(values.fechaCreacion);

    const entity = {
      ...comentarioEntity,
      ...values,
      publicacion: publicacions.find(it => it.id.toString() === values.publicacion?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          fechaCreacion: displayDefaultDateTime(),
        }
      : {
          ...comentarioEntity,
          fechaCreacion: convertDateTimeFromServer(comentarioEntity.fechaCreacion),
          publicacion: comentarioEntity?.publicacion?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="miPrimeraApp.comentario.home.createOrEditLabel" data-cy="ComentarioCreateUpdateHeading">
            <Translate contentKey="miPrimeraApp.comentario.home.createOrEditLabel">Create or edit a Comentario</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="comentario-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('miPrimeraApp.comentario.texto')}
                id="comentario-texto"
                name="texto"
                data-cy="texto"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('miPrimeraApp.comentario.fechaCreacion')}
                id="comentario-fechaCreacion"
                name="fechaCreacion"
                data-cy="fechaCreacion"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="comentario-publicacion"
                name="publicacion"
                data-cy="publicacion"
                label={translate('miPrimeraApp.comentario.publicacion')}
                type="select"
              >
                <option value="" key="0" />
                {publicacions
                  ? publicacions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.titulo}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/comentario" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ComentarioUpdate;
