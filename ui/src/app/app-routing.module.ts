import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WorkersComponent } from './workers/workers.component';
import { WorkerComponent } from './workers/worker/worker.component';
import { DoorsComponent } from './doors/doors/doors.component';
import { DoorComponent } from './doors/door/door.component';

const routes: Routes = [
  { path: '', redirectTo: '/workers', pathMatch: 'full' },
  { path: 'workers', component: WorkersComponent },
  { path: 'workers/:id', component: WorkerComponent },
  { path: 'doors', component: DoorsComponent },
  { path: 'doors/:id', component: DoorComponent },
  {
    path: 'incidents',
    loadChildren: 'app/incidents/incidents.module#IncidentsModule',
    data: { preload: false }
  },
  {
    path: 'scores',
    loadChildren: 'app/scores/scores.module#ScoresModule',
    data: { preload: false }
  },
  {
    path: 'cards',
    loadChildren: 'app/cards/cards.module#CardsModule',
    data: { preload: false }
  },
  {
    path: 'policies',
    loadChildren: 'app/policies/policies.module#PoliciesModule',
    data: { preload: false }
  },
  {
    path: 'users',
    loadChildren: 'app/users/users.module#UsersModule',
    data: { preload: false }
  }
];
@NgModule({
  exports: [RouterModule],
  imports: [RouterModule.forRoot(routes)]
})
export class AppRoutingModule {

}
