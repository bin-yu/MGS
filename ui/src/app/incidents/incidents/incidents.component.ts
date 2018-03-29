import { DialogComponent } from './../../share/dialog/dialog.component';
import { AuthService } from './../../backend/auth/auth.service';
import { Router } from '@angular/router';
import { Incident, Pageable, IncidentService } from './../../backend/backend.module';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MessageService } from '../../messages/messages.module';
import { PageableComponent } from '../../share/share.module';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-incidents',
  templateUrl: './incidents.component.html',
  styleUrls: ['./incidents.component.scss']
})
export class IncidentsComponent extends PageableComponent implements OnInit {
  @ViewChild('dialog') dialog: DialogComponent;
  domainId: number;
  incidents: Incident[];
  selectedIncidents: Set<Incident>;

  searchStr: string;
  isAdmin: boolean;
  constructor(private route: ActivatedRoute, private router: Router,
    private incidentSrv: IncidentService, private msgSrv: MessageService, authSrv: AuthService) {
    super();
    this.isAdmin = authSrv.isAdmin();
    this.selectedIncidents = new Set<Incident>();
    route.params.subscribe(
      val => {
        const domainId = + val.domainId;
        if (domainId && !isNaN(domainId)) {
          this.domainId = domainId;
          this.reloadItems();
        }
      }
    );
  }
  ngOnInit() {
  }
  reloadItems(): void {
    if (this.searchStr && this.searchStr.length > 0) {
      this.incidentSrv.findIncidents(this.domainId, this.searchStr, this.pageable).subscribe(
        resp => {
          this.totalItems = resp.totalElements;
          this.incidents = resp.content;
        }
      );
    } else {
      this.incidentSrv.getIncidentsx(this.domainId, this.pageable).subscribe(
        resp => {
          this.totalItems = resp.totalElements;
          this.incidents = resp.content;
        }
      );
    }
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
    const obs = [];
    this.selectedIncidents.forEach(
      (value: Incident, value2: Incident, set: Set<Incident>) => {
        obs.push(this.incidentSrv.deleteIncident(this.domainId, value).map(
          _ => {
            console.log('incident deleted: ' + value.id);
          }
        ));
      }
    );
    this.dialog.startWorks('删除事件', obs).subscribe(
      results => {
        this.reloadItems();
      }
    );
  }
  performSearch(event: any): void {
    if (event.code === 'Enter') {
      this.searchStr = event.target.value;
      console.log('Searching incidents with worker name : "' + this.searchStr + '"');
      this.reloadItems();
    }
  }

}
