import { DialogComponent } from './../../share/dialog/dialog.component';
import { MessageService } from './../../messages/message.service';
import { UserService, User } from './../../backend/backend.module';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { PageableComponent } from '../../share/share.module';
@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent extends PageableComponent implements OnInit {
  @ViewChild('dialog') dialog: DialogComponent;
  domainId: number;
  users: User[];
  selectedUsers: Set<User>;

  searchStr: string;

  constructor(private route: ActivatedRoute, private router: Router, private userSrv: UserService, private msgSrv: MessageService) {
    super();
    this.selectedUsers = new Set<User>();
    route.params.subscribe(
      val => {
        const domainId = + val.domainId;
        if (domainId && !isNaN(domainId)) {
          this.domainId = domainId;
          this.reloadItems();
        }
      }
    );
  }

  ngOnInit() {
  }
  reloadItems(): void {
    if (this.searchStr && this.searchStr.length > 0) {
      this.userSrv.findUsersx(this.domainId, this.searchStr, this.pageable).subscribe(
        resp => {
          this.totalItems = resp.totalElements;
          this.users = resp.content;
        }
      );
    } else {
      this.userSrv.getUsersx(this.domainId, this.pageable).toPromise().then(
        resp => {
          this.totalItems = resp.totalElements;
          this.users = resp.content;
        }
      );
    }
    this.selectedUsers.clear();
  }
  handleSelectEvent(e, user: User) {
    if (e.target.checked) {
      this.selectedUsers.add(user);
    } else {
      this.selectedUsers.delete(user);
    }
  }
  performDelete(): void {
    const obs = [];
    this.selectedUsers.forEach(
      (value: User, value2: User, set: Set<User>) => {
        obs.push(this.userSrv.deleteUser(this.domainId, value).map(
          _ => {
            console.log('user deleted: ' + value.id);
          }
        ));
      }
    );
    this.dialog.startWorks('删除用户', obs).subscribe(
      _ => {
        this.reloadItems();
      }
    );
  }
  performSearch(event: any): void {
    if (event.code === 'Enter') {
      this.searchStr = event.target.value;
      console.log('Searching users with "' + this.searchStr + '"');
      this.reloadItems();
    }
  }
}
