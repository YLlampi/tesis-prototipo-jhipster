import publicacion from 'app/entities/publicacion/publicacion.reducer';
import comentario from 'app/entities/comentario/comentario.reducer';
import autor from 'app/entities/autor/autor.reducer';
import etiqueta from 'app/entities/etiqueta/etiqueta.reducer';
import adjunto from 'app/entities/adjunto/adjunto.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  publicacion,
  comentario,
  autor,
  etiqueta,
  adjunto,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
