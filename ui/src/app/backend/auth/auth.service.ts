import { of } from 'rxjs/observable/of';
import { environment } from './../../../environments/environment';
import { BackendService } from './../backend.service';
import { UserService } from './../user/user.service';
import { Injectable } from '@angular/core';
import { User, Role, ROLE_USER, ROLE_ADMIN } from '../user/user';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class AuthService {
  _loginUser: User;
  constructor(private backend: BackendService) { }
  init(): Observable<User> {
    let ob: Observable<User>;
    if (environment.mockUser) {
      ob = of(User.clone(environment.mockUser));
    } else {
      ob = this.backend.getx<User>('/authInfo');
    }
    return ob.map(
      user => {
        if (user) {
          this._loginUser = user;
        }
        return user;
      }
    );
  }

  logout(): Observable<void> {
    return this.backend.post<void, void>('/logout', null);
  }

  get loginUser(): User {
    return this._loginUser;
  }
  get loginRole(): string {
    if (this.loginUser) {
      return this.loginUser.role;
    } else {
      return ROLE_USER;
    }
  }
  public isAdmin(): boolean {
    return this.loginRole === ROLE_ADMIN;
  }
}
