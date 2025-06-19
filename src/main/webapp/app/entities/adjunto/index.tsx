import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Adjunto from './adjunto';
import AdjuntoDetail from './adjunto-detail';
import AdjuntoUpdate from './adjunto-update';
import AdjuntoDeleteDialog from './adjunto-delete-dialog';

const AdjuntoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Adjunto />} />
    <Route path="new" element={<AdjuntoUpdate />} />
    <Route path=":id">
      <Route index element={<AdjuntoDetail />} />
      <Route path="edit" element={<AdjuntoUpdate />} />
      <Route path="delete" element={<AdjuntoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AdjuntoRoutes;
