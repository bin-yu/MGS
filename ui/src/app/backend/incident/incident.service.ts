import { Pageable } from './../pageable';
import { Injectable } from '@angular/core';
import { BackendService } from '../backend.service';
import { Observable } from 'rxjs/Observable';
import { Incident } from './incident';
import { PagedResp } from '../paged-resp';

@Injectable()
export class IncidentService {

  constructor(private backend: BackendService) { }
  getIncidents(): Observable<Incident[]> {
    return this.backend.list<Incident>('/incidents');
  }
  getIncidentsx(pageable: Pageable): Observable<PagedResp<Incident>> {
    return this.backend.listx<Incident>('/incidents', pageable);
  }
  getIncident(id: Number): Observable<Incident> {
    return this.backend.get<Incident>('/incidents/' + id);
  }
  addIncident(incident: Incident): Observable<Incident> {
    return this.backend.post<Incident, Incident>('/incidents', incident);
  }
  updateIncident(incident: Incident): Observable<Incident> {
    return this.backend.put<Incident>('/incidents/' + incident.id, incident);
  }
  deleteIncident(incident: Incident): Observable<void> {
    return this.backend.delete<void>('/incidents/' + incident.id);
  }

}
