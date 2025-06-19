import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedBlobField, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getPublicacions } from 'app/entities/publicacion/publicacion.reducer';
import { createEntity, getEntity, reset, updateEntity } from './adjunto.reducer';

export const AdjuntoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const publicacions = useAppSelector(state => state.publicacion.entities);
  const adjuntoEntity = useAppSelector(state => state.adjunto.entity);
  const loading = useAppSelector(state => state.adjunto.loading);
  const updating = useAppSelector(state => state.adjunto.updating);
  const updateSuccess = useAppSelector(state => state.adjunto.updateSuccess);

  const handleClose = () => {
    navigate('/adjunto');
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

    const entity = {
      ...adjuntoEntity,
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
      ? {}
      : {
          ...adjuntoEntity,
          publicacion: adjuntoEntity?.publicacion?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="miPrimeraApp.adjunto.home.createOrEditLabel" data-cy="AdjuntoCreateUpdateHeading">
            <Translate contentKey="miPrimeraApp.adjunto.home.createOrEditLabel">Create or edit a Adjunto</Translate>
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
                  id="adjunto-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('miPrimeraApp.adjunto.nombreArchivo')}
                id="adjunto-nombreArchivo"
                name="nombreArchivo"
                data-cy="nombreArchivo"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedBlobField
                label={translate('miPrimeraApp.adjunto.archivo')}
                id="adjunto-archivo"
                name="archivo"
                data-cy="archivo"
                isImage
                accept="image/*"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('miPrimeraApp.adjunto.tipoMime')}
                id="adjunto-tipoMime"
                name="tipoMime"
                data-cy="tipoMime"
                type="text"
              />
              <ValidatedField
                id="adjunto-publicacion"
                name="publicacion"
                data-cy="publicacion"
                label={translate('miPrimeraApp.adjunto.publicacion')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/adjunto" replace color="info">
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

export default AdjuntoUpdate;
