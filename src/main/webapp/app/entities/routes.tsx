import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Publicacion from './publicacion';
import Comentario from './comentario';
import Autor from './autor';
import Etiqueta from './etiqueta';
import Adjunto from './adjunto';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="publicacion/*" element={<Publicacion />} />
        <Route path="comentario/*" element={<Comentario />} />
        <Route path="autor/*" element={<Autor />} />
        <Route path="etiqueta/*" element={<Etiqueta />} />
        <Route path="adjunto/*" element={<Adjunto />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
