import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Etiqueta from './etiqueta';
import EtiquetaDetail from './etiqueta-detail';
import EtiquetaUpdate from './etiqueta-update';
import EtiquetaDeleteDialog from './etiqueta-delete-dialog';

const EtiquetaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Etiqueta />} />
    <Route path="new" element={<EtiquetaUpdate />} />
    <Route path=":id">
      <Route index element={<EtiquetaDetail />} />
      <Route path="edit" element={<EtiquetaUpdate />} />
      <Route path="delete" element={<EtiquetaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EtiquetaRoutes;
