import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorkerService } from './worker/worker.service';
import { DoorService } from './door/door.service';
import { BackendService } from './backend.service';
import { IncidentService } from './incident/incident.service';
import { PolicyService } from './policy/policy.service';
import { UserService } from './user/user.service';




export { WorkerService } from './worker/worker.service';
export { Worker } from './worker/worker';
export { DoorService } from './door/door.service';
export { Door } from './door/door';
export { Card } from './door/card';
export { CardAreaStatus } from './door/cardAreaStatus';
export { CardData } from './door/cardData';
export { IncidentService } from './incident/incident.service';
export { Incident } from './incident/incident';
export { Pageable } from './pageable';
export { PolicyService } from './policy/policy.service';
export { Policy, Action } from './policy/policy';
export { Condition, PropertyName, Comparator } from './policy/condition';
export { UserService } from './user/user.service';
export { User, Role } from './user/user';
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [],
  providers: [
    WorkerService, DoorService, BackendService, IncidentService, PolicyService, UserService]
})
export class BackendModule { }
