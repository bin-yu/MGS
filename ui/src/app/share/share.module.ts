import { DomainTreeComponent } from './domain-tree/domain-tree.component';
import { FormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaginationComponent } from './pagination/pagination.component';
import { NgbModule, NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { EnumToArrayPipe } from './enum-to-array-pipe';
import { WorkerInputComponent } from './worker-input/worker-input.component';
import { jqxTreeGridComponent } from 'jqwidgets-scripts/jqwidgets-ts/angular_jqxtreegrid';
import { DialogComponent } from './dialog/dialog.component';
import { jqxProgressBarComponent } from 'jqwidgets-scripts/jqwidgets-ts/angular_jqxprogressbar';

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
  declarations: [jqxTreeGridComponent, jqxProgressBarComponent,
    PaginationComponent, EnumToArrayPipe, WorkerInputComponent, DomainTreeComponent, DialogComponent],
  exports: [PaginationComponent, EnumToArrayPipe, WorkerInputComponent, DomainTreeComponent, DialogComponent]
})
export class ShareModule { }
