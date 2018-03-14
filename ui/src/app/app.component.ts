import { AuthService } from './backend/auth/auth.service';
import { Location } from '@angular/common';
import { UserService, User } from './backend/backend.module';
import * as $ from 'jquery';
import { Component, Inject, OnInit } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';
import { Role } from './backend/user/user';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  scoreIsCollapsed = true;
  doorIsCollapsed = true;
  allIsCollapsed = false;
  loginUser: User;
  constructor(private authSrv: AuthService, @Inject(DOCUMENT) private document: any, private location: Location) {
    this.authSrv.init().subscribe(
      loginUser => {
        this.loginUser = loginUser;
      }
    );
  }
  ngOnInit() {
  }
  get loginRole(): String {
    return this.authSrv.loginRole;
  }
  toggleSideBar(): void {
    this.allIsCollapsed = !this.allIsCollapsed;
  }
  toggleSubMenu(flag: boolean): boolean {
    if (this.allIsCollapsed) {
      // when side bar is hidden, show the submenu, show the side bar
      flag = false;
      this.allIsCollapsed = false;
    } else {
      // when side bar is shown, reverse the toggle state
      flag = !flag;
    }
    return flag;
  }
  logout(): void {
    this.authSrv.logout().subscribe(
      _ => {
        this.document.location.href = this.document.baseURI + 'login';
      }
    );
  }
}
