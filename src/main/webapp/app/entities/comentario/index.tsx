import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Comentario from './comentario';
import ComentarioDetail from './comentario-detail';
import ComentarioUpdate from './comentario-update';
import ComentarioDeleteDialog from './comentario-delete-dialog';

const ComentarioRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Comentario />} />
    <Route path="new" element={<ComentarioUpdate />} />
    <Route path=":id">
      <Route index element={<ComentarioDetail />} />
      <Route path="edit" element={<ComentarioUpdate />} />
      <Route path="delete" element={<ComentarioDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ComentarioRoutes;
