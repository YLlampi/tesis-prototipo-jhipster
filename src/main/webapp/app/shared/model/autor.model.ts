import { IUser } from 'app/shared/model/user.model';

export interface IAutor {
  id?: number;
  nombre?: string;
  email?: string;
  user?: IUser | null;
}

export const defaultValue: Readonly<IAutor> = {};
