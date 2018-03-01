import { Injectable } from '@angular/core';
import { Pageable } from './../pageable';
import { BackendService } from '../backend.service';
import { Observable } from 'rxjs/Observable';
import { map } from 'rxjs/operators';
import { PagedResp } from '../paged-resp';
import { User } from './user';
@Injectable()
export class UserService {

  constructor(private backend: BackendService) { }
  /* getUsers(): Observable<User[]> {
    return this.backend.list<User>('/users');
  } */
  getUsersx(pageable: Pageable): Observable<PagedResp<User>> {
    return this.backend.listx<User>('/users', pageable).pipe(
      map(resp => {
        const users = [];
        resp.content.forEach(p => { users.push(User.clone(p)); });
        resp.content = users;
        return resp;
      })
    );
  }
  findUsersx(nameLike: string, pageable: Pageable): Observable<PagedResp<User>> {
    return this.backend.getx<PagedResp<User>>('/users/search', {
      params: {
        nameLike: '%' + nameLike + '%',
        page: '' + pageable.page,
        size: '' + pageable.size,
        sort: pageable.sort
      }
    }).pipe(
      map(resp => {
        const users = [];
        resp.content.forEach(p => { users.push(User.clone(p)); });
        resp.content = users;
        return resp;
      })
    );
  }
  getUser(id: Number): Observable<User> {
    return this.backend.get<User>('/users/' + id).pipe(
      map(user => User.clone(user))
    );
  }
  addUser(user: User): Observable<User> {
    return this.backend.post<User, User>('/users', user).pipe(
      map(u => User.clone(u))
    );
  }
  updateUser(user: User): Observable<User> {
    return this.backend.put<User>('/users/' + user.id, user).pipe(
      map(u => User.clone(u))
    );
  }
  deleteUser(user: User): Observable<void> {
    return this.backend.delete<void>('/users/' + user.id);
  }

  logout(): Observable<void> {
    return this.backend.post<void, void>('/logout', null);
  }

  checkLoginStatus(): Observable<string> {
    return this.backend.getx<string>('/status', { responseType: 'text' });
  }
}
