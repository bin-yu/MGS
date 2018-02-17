import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaginationComponent } from './pagination/pagination.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { EnumToArrayPipe } from './enum-to-array-pipe';

export { PaginationComponent } from './pagination/pagination.component';
export { PageableComponent } from './pageable-component';
@NgModule({
  imports: [
    CommonModule,
    NgbModule
  ],
  declarations: [PaginationComponent, EnumToArrayPipe],
  exports: [PaginationComponent, EnumToArrayPipe]
})
export class ShareModule { }
