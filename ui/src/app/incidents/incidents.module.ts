import { IncidentsMainComponent } from './incidents.main.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IncidentsComponent } from './incidents/incidents.component';
import { AppRoutingModule } from '../app-routing.module';
import { RouterModule, Routes } from '@angular/router';
import { IncidentComponent } from './incident/incident.component';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ShareModule } from '../share/share.module';

export { IncidentsComponent } from './incidents/incidents.component';
export { Policy, Action } from '../backend/policy/policy';
export { Condition } from '../backend/policy/condition';

const IncRoutes: Routes = [
  {
    path: '',
    component: IncidentsMainComponent,
    children: [
      {
        path: '',
        component: IncidentsComponent
      },
      {
        path: ':domainId',
        component: IncidentsComponent
      },
      { path: ':domainId/:id', component: IncidentComponent }
    ]
  }
];

@NgModule({
  imports: [
    NgbModule,
    CommonModule,
    FormsModule,
    RouterModule.forChild(IncRoutes),
    ShareModule
  ],
  exports: [
    RouterModule
  ],
  declarations: [
    IncidentsMainComponent, IncidentsComponent, IncidentComponent],
  providers: []
})
export class IncidentsModule { }
