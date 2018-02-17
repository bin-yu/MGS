import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersMainComponent } from './users-main/users-main.component';
import { UsersComponent } from './users/users.component';
import { UserComponent } from './user/user.component';
import { ShareModule } from './../share/share.module';
import { FormsModule } from '@angular/forms';
import { Routes, RouterModule } from '@angular/router';

const PolRoutes: Routes = [
  {
    path: '',
    component: UsersMainComponent,
    children: [
      {
        path: '',
        component: UsersComponent
      },
      { path: ':id', component: UserComponent }
    ]
  }
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ShareModule,
    RouterModule.forChild(PolRoutes)
  ],
  declarations: [UsersMainComponent, UsersComponent, UserComponent]
})
export class UsersModule { }
