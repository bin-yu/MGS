import { ShareModule } from './../share/share.module';
import { AppRoutingModule } from './../app-routing.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DoorsComponent } from './doors/doors.component';
import { DoorComponent } from './door/door.component';
import { FormsModule } from '@angular/forms';
import { CardBlacklistComponent } from './card-blacklist/card-blacklist.component';

export { DoorsComponent } from './doors/doors.component';
export { DoorComponent } from './door/door.component';
export { CardBlacklistComponent } from './card-blacklist/card-blacklist.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    AppRoutingModule,
    ShareModule
  ],
  declarations: [DoorsComponent, DoorComponent, CardBlacklistComponent],
  exports: []
})
export class DoorsModule { }
