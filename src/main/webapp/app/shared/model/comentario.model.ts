import dayjs from 'dayjs';
import { IPublicacion } from 'app/shared/model/publicacion.model';

export interface IComentario {
  id?: number;
  texto?: string;
  fechaCreacion?: dayjs.Dayjs | null;
  publicacion?: IPublicacion | null;
}

export const defaultValue: Readonly<IComentario> = {};
