import dayjs from 'dayjs';
import { IEtiqueta } from 'app/shared/model/etiqueta.model';
import { IAutor } from 'app/shared/model/autor.model';

export interface IPublicacion {
  id?: number;
  titulo?: string;
  contenido?: string;
  fechaPublicacion?: dayjs.Dayjs | null;
  etiquetas?: IEtiqueta[] | null;
  autor?: IAutor | null;
}

export const defaultValue: Readonly<IPublicacion> = {};
