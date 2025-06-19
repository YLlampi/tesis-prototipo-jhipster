import { IPublicacion } from 'app/shared/model/publicacion.model';

export interface IEtiqueta {
  id?: number;
  nombre?: string;
  publicacions?: IPublicacion[] | null;
}

export const defaultValue: Readonly<IEtiqueta> = {};
