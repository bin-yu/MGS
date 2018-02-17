import { MessageService } from './../../messages/message.service';
import { UserService, User } from './../../backend/backend.module';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PageableComponent } from '../../share/share.module';
@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent extends PageableComponent implements OnInit {

  users: User[];
  selectedUsers: Set<User>;
  constructor(private router: Router, private userSrv: UserService, private msgSrv: MessageService) {
    super();
    this.selectedUsers = new Set<User>();
  }

  ngOnInit() {
    this.reloadItems();
  }
  reloadItems(): void {
    this.userSrv.getUsersx(this.pageable).toPromise().then(
      resp => {
        this.totalItems = resp.totalElements;
        this.users = resp.content;
      }
    );
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
    this.selectedUsers.forEach(
      (value: User, value2: User, set: Set<User>) => {
        this.userSrv.deleteUser(value).subscribe(
          _ => {
            console.log('user deleted: ' + value.id);
            this.msgSrv.addSuccess('用户删除成功：' + value.id);
            this.reloadItems();
          }
        );
      }
    );
  }

}
