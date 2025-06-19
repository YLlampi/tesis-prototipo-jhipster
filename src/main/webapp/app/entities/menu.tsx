import React from 'react';
import { Translate } from 'react-jhipster'; // eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/publicacion">
        <Translate contentKey="global.menu.entities.publicacion" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/comentario">
        <Translate contentKey="global.menu.entities.comentario" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/autor">
        <Translate contentKey="global.menu.entities.autor" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/etiqueta">
        <Translate contentKey="global.menu.entities.etiqueta" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/adjunto">
        <Translate contentKey="global.menu.entities.adjunto" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
