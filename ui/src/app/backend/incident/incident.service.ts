import { Pageable } from './../pageable';
import { Injectable } from '@angular/core';
import { BackendService } from '../backend.service';
import { Observable } from 'rxjs/Observable';
import { Incident } from './incident';
import { PagedResp } from '../paged-resp';

@Injectable()
export class IncidentService {

  constructor(private backend: BackendService) { }
  getIncidents(domainId: number): Observable<Incident[]> {
    return this.backend.list<Incident>('/domains/' + domainId + '/incidents');
  }
  getIncidentsx(domainId: number, pageable: Pageable): Observable<PagedResp<Incident>> {
    return this.backend.listx<Incident>('/domains/' + domainId + '/incidents', pageable);
  }
  getIncident(domainId: number, id: Number): Observable<Incident> {
    return this.backend.get<Incident>('/domains/' + domainId + '/incidents/' + id);
  }
  addIncident(domainId: number, incident: Incident): Observable<Incident> {
    return this.backend.post<Incident, Incident>('/domains/' + domainId + '/incidents', incident);
  }
  updateIncident(domainId: number, incident: Incident): Observable<Incident> {
    return this.backend.put<Incident>('/domains/' + domainId + '/incidents/' + incident.id, incident);
  }
  deleteIncident(domainId: number, incident: Incident): Observable<void> {
    return this.backend.delete<void>('/domains/' + domainId + '/incidents/' + incident.id);
  }

}
