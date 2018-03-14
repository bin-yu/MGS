import { DoorsMainComponent } from './doors-main/doors.main.component';
import { ShareModule } from './../share/share.module';
import { AppRoutingModule } from './../app-routing.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DoorsComponent } from './doors/doors.component';
import { DoorComponent } from './door/door.component';
import { FormsModule } from '@angular/forms';
import { CardBlacklistComponent } from './card-blacklist/card-blacklist.component';
import { Routes, RouterModule } from '@angular/router';

export { DoorsComponent } from './doors/doors.component';
export { DoorComponent } from './door/door.component';
export { CardBlacklistComponent } from './card-blacklist/card-blacklist.component';

const DoorRoutes: Routes = [
  {
    path: '',
    component: DoorsMainComponent,
    children: [
      {
        path: '',
        component: DoorsComponent
      },
      {
        path: ':domainId',
        component: DoorsComponent
      },
      { path: ':domainId/:id', component: DoorComponent }
    ]
  }
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ShareModule,
    RouterModule.forChild(DoorRoutes)
  ],
  declarations: [DoorsMainComponent, DoorsComponent, DoorComponent, CardBlacklistComponent],
  exports: []
})
export class DoorsModule { }
