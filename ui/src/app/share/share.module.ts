import { FormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaginationComponent } from './pagination/pagination.component';
import { NgbModule, NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { EnumToArrayPipe } from './enum-to-array-pipe';
import { WorkerInputComponent } from './worker-input/worker-input.component';

export { PaginationComponent } from './pagination/pagination.component';
export { WorkerInputComponent } from './worker-input/worker-input.component';
export { PageableComponent } from './pageable-component';
@NgModule({
  imports: [
    CommonModule,
    NgbModule,
    FormsModule,
    NgbTypeaheadModule
  ],
  declarations: [PaginationComponent, EnumToArrayPipe, WorkerInputComponent],
  exports: [PaginationComponent, EnumToArrayPipe, WorkerInputComponent]
})
export class ShareModule { }
