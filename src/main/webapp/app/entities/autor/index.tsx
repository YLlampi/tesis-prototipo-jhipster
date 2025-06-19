import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Autor from './autor';
import AutorDetail from './autor-detail';
import AutorUpdate from './autor-update';
import AutorDeleteDialog from './autor-delete-dialog';

const AutorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Autor />} />
    <Route path="new" element={<AutorUpdate />} />
    <Route path=":id">
      <Route index element={<AutorDetail />} />
      <Route path="edit" element={<AutorUpdate />} />
      <Route path="delete" element={<AutorDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AutorRoutes;
