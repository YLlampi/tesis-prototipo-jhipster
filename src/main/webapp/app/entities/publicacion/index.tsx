import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Publicacion from './publicacion';
import PublicacionDetail from './publicacion-detail';
import PublicacionUpdate from './publicacion-update';
import PublicacionDeleteDialog from './publicacion-delete-dialog';

const PublicacionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Publicacion />} />
    <Route path="new" element={<PublicacionUpdate />} />
    <Route path=":id">
      <Route index element={<PublicacionDetail />} />
      <Route path="edit" element={<PublicacionUpdate />} />
      <Route path="delete" element={<PublicacionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PublicacionRoutes;
