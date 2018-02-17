import { Router } from '@angular/router';
import { Incident, Pageable, IncidentService } from './../../backend/backend.module';
import { Component, OnInit } from '@angular/core';
import { MessageService } from '../../messages/messages.module';
import { PageableComponent } from '../../share/share.module';

@Component({
  selector: 'app-incidents',
  templateUrl: './incidents.component.html',
  styleUrls: ['./incidents.component.scss']
})
export class IncidentsComponent extends PageableComponent implements OnInit {

  incidents: Incident[];
  selectedIncidents: Set<Incident>;
  constructor(private router: Router, private incidentSrv: IncidentService, private msgSrv: MessageService) {
    super();
    this.selectedIncidents = new Set<Incident>();
  }

  ngOnInit() {
    this.reloadItems();
  }
  reloadItems(): void {
    this.incidentSrv.getIncidentsx(this.pageable).subscribe(
      resp => {
        this.totalItems = resp.totalElements;
        this.incidents = resp.content;
      }
    );
    this.selectedIncidents.clear();
  }
  handleSelectEvent(e, incident: Incident) {
    if (e.target.checked) {
      this.selectedIncidents.add(incident);
    } else {
      this.selectedIncidents.delete(incident);
    }
  }

  performDelete(): void {
    this.selectedIncidents.forEach(
      (value: Incident, value2: Incident, set: Set<Incident>) => {
        this.incidentSrv.deleteIncident(value).subscribe(
          _ => {
            console.log('incident deleted: ' + value.id);
            this.msgSrv.addSuccess('事件删除成功：' + value.id);
            this.reloadItems();
          }
        );
      }
    );
  }


}
