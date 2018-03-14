import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { UserService, User, Role } from './../../backend/backend.module';
@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  domainId: number;
  isAdd: boolean;
  user: User;
  roles = Object.entries(Role);
  constructor(private route: ActivatedRoute, private _location: Location, private userSrv: UserService) {
    this.user = new User();
  }

  ngOnInit() {
    this.domainId = +this.route.snapshot.paramMap.get('domainId');
    const id = +this.route.snapshot.paramMap.get('id');
    if (Number.isNaN(id)) {
      this.isAdd = true;
      return;
    }
    this.isAdd = false;
    this.userSrv.getUser(this.domainId, id).subscribe(
      user => {
        this.user = user;
      }
    );
  }

  onSubmit(): void {
    if (this.isAdd) {
      this.userSrv.addUser(this.domainId, this.user).subscribe(
        user => {
          this._location.back();
        }
      );
    } else {
      // update user
      this.userSrv.updateUser(this.domainId, this.user).subscribe(
        user => {
          this._location.back();
        }
      );
    }
  }

}
