import { WorkersMainComponent } from './workers.main.component';

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <-- NgModel lives here
import { WorkersComponent } from './workers.component';
import { WorkerComponent } from './worker/worker.component';
import { AppRoutingModule } from '../app-routing.module';
import { ShareModule } from '../share/share.module';
import { Routes, RouterModule } from '@angular/router';



export { WorkersComponent } from './workers.component';
export { WorkerComponent } from './worker/worker.component';

const WorkerRoutes: Routes = [
  {
    path: '',
    component: WorkersMainComponent,
    children: [
      {
        path: '',
        component: WorkersComponent
      },
      { path: ':id', component: WorkerComponent }
    ]
  }
];
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ShareModule,
    RouterModule.forChild(WorkerRoutes)
  ],
  declarations: [WorkersMainComponent, WorkersComponent, WorkerComponent],
  providers: []
})
export class WorkersModule { }
