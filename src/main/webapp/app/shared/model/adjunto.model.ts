import { IPublicacion } from 'app/shared/model/publicacion.model';

export interface IAdjunto {
  id?: number;
  nombreArchivo?: string;
  archivoContentType?: string;
  archivo?: string;
  tipoMime?: string | null;
  publicacion?: IPublicacion | null;
}

export const defaultValue: Readonly<IAdjunto> = {};
