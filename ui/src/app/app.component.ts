import { Location } from '@angular/common';
import { UserService } from './backend/user/user.service';
import * as $ from 'jquery';
import { Component, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  scoreIsCollapsed = true;
  doorIsCollapsed = true;
  allIsCollapsed = false;
  constructor(private useSrv: UserService, @Inject(DOCUMENT) private document: any, private location: Location) { }
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
    this.useSrv.logout().subscribe(
      _ => {
        this.document.location.href = this.document.baseURI + 'login';
      }
    );
  }
}
