
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <-- NgModel lives here
import { WorkersComponent } from './workers.component';
import { WorkerComponent } from './worker/worker.component';
import { AppRoutingModule } from '../app-routing.module';
import { ShareModule } from '../share/share.module';



export { WorkersComponent } from './workers.component';
export { WorkerComponent } from './worker/worker.component';

@NgModule({
  imports: [
    CommonModule,
    AppRoutingModule,
    FormsModule,
    ShareModule
  ],
  declarations: [WorkersComponent, WorkerComponent],
  providers: []
})
export class WorkersModule { }
