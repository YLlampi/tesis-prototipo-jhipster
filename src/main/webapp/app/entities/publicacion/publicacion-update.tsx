import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getEtiquetas } from 'app/entities/etiqueta/etiqueta.reducer';
import { getEntities as getAutors } from 'app/entities/autor/autor.reducer';
import { createEntity, getEntity, reset, updateEntity } from './publicacion.reducer';

export const PublicacionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const etiquetas = useAppSelector(state => state.etiqueta.entities);
  const autors = useAppSelector(state => state.autor.entities);
  const publicacionEntity = useAppSelector(state => state.publicacion.entity);
  const loading = useAppSelector(state => state.publicacion.loading);
  const updating = useAppSelector(state => state.publicacion.updating);
  const updateSuccess = useAppSelector(state => state.publicacion.updateSuccess);

  const handleClose = () => {
    navigate('/publicacion');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getEtiquetas({}));
    dispatch(getAutors({}));
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
    values.fechaPublicacion = convertDateTimeToServer(values.fechaPublicacion);

    const entity = {
      ...publicacionEntity,
      ...values,
      etiquetas: mapIdList(values.etiquetas),
      autor: autors.find(it => it.id.toString() === values.autor?.toString()),
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
          fechaPublicacion: displayDefaultDateTime(),
        }
      : {
          ...publicacionEntity,
          fechaPublicacion: convertDateTimeFromServer(publicacionEntity.fechaPublicacion),
          etiquetas: publicacionEntity?.etiquetas?.map(e => e.id.toString()),
          autor: publicacionEntity?.autor?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="miPrimeraApp.publicacion.home.createOrEditLabel" data-cy="PublicacionCreateUpdateHeading">
            <Translate contentKey="miPrimeraApp.publicacion.home.createOrEditLabel">Create or edit a Publicacion</Translate>
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
                  id="publicacion-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('miPrimeraApp.publicacion.titulo')}
                id="publicacion-titulo"
                name="titulo"
                data-cy="titulo"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 5, message: translate('entity.validation.minlength', { min: 5 }) },
                }}
              />
              <ValidatedField
                label={translate('miPrimeraApp.publicacion.contenido')}
                id="publicacion-contenido"
                name="contenido"
                data-cy="contenido"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('miPrimeraApp.publicacion.fechaPublicacion')}
                id="publicacion-fechaPublicacion"
                name="fechaPublicacion"
                data-cy="fechaPublicacion"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('miPrimeraApp.publicacion.etiqueta')}
                id="publicacion-etiqueta"
                data-cy="etiqueta"
                type="select"
                multiple
                name="etiquetas"
              >
                <option value="" key="0" />
                {etiquetas
                  ? etiquetas.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nombre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="publicacion-autor"
                name="autor"
                data-cy="autor"
                label={translate('miPrimeraApp.publicacion.autor')}
                type="select"
              >
                <option value="" key="0" />
                {autors
                  ? autors.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nombre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/publicacion" replace color="info">
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

export default PublicacionUpdate;
