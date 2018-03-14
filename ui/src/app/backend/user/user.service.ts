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
  getUsersx(domainId: number, pageable: Pageable): Observable<PagedResp<User>> {
    return this.backend.listx<User>('/domains/' + domainId + '/users', pageable).pipe(
      map(resp => {
        const users = [];
        resp.content.forEach(p => { users.push(User.clone(p)); });
        resp.content = users;
        return resp;
      })
    );
  }
  findUsersx(domainId: number, nameLike: string, pageable: Pageable): Observable<PagedResp<User>> {
    return this.backend.getx<PagedResp<User>>('/domains/' + domainId + '/users/search', {
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
  getUser(domainId: number, id: Number): Observable<User> {
    return this.backend.get<User>('/domains/' + domainId + '/users/' + id).pipe(
      map(user => User.clone(user))
    );
  }
  addUser(domainId: number, user: User): Observable<User> {
    return this.backend.post<User, User>('/domains/' + domainId + '/users', user).pipe(
      map(u => User.clone(u))
    );
  }
  updateUser(domainId: number, user: User): Observable<User> {
    return this.backend.put<User>('/domains/' + domainId + '/users/' + user.id, user).pipe(
      map(u => User.clone(u))
    );
  }
  deleteUser(domainId: number, user: User): Observable<void> {
    return this.backend.delete<void>('/domains/' + domainId + '/users/' + user.id);
  }


  checkLoginStatus(): Observable<string> {
    return this.backend.getx<string>('/status', { responseType: 'text' });
  }
}
