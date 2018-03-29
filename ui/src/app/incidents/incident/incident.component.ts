import { DialogComponent } from './../../share/dialog/dialog.component';
import { Injectable, Component, OnInit, ViewChild } from '@angular/core';
import { Incident, IncidentService } from '../../backend/backend.module';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { Worker } from '../../backend/backend.module';
import { Location } from '@angular/common';
import { NgbDateStruct, NgbDateAdapter, NgbDatepickerI18n } from '@ng-bootstrap/ng-bootstrap';

const I18N_VALUES = {
  'zh-cn': {
    weekdays: ['一', '二', '三', '四', '五', '六', '日'],
    months: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'],
  }
  // other languages you would support
};
@Injectable()
export class I18n {
  language = 'zh-cn';
}
// Define custom service providing the months and weekdays translations
@Injectable()
export class CustomDatepickerI18n extends NgbDatepickerI18n {

  constructor(private _i18n: I18n) {
    super();
  }

  getWeekdayShortName(weekday: number): string {
    return I18N_VALUES[this._i18n.language].weekdays[weekday - 1];
  }
  getMonthShortName(month: number): string {
    return I18N_VALUES[this._i18n.language].months[month - 1];
  }
  getMonthFullName(month: number): string {
    return this.getMonthShortName(month);
  }
}
/**
 * Example of a Native Date adapter
 */
@Injectable()
export class NgbDateNativeAdapter extends NgbDateAdapter<Date> {

  fromModel(date: Date): NgbDateStruct {
    return (date && date.getFullYear) ? { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() } : null;
  }

  toModel(date: NgbDateStruct): Date {
    return date ? new Date(date.year, date.month - 1, date.day) : null;
  }
}

@Component({
  selector: 'app-incident',
  templateUrl: './incident.component.html',
  styleUrls: ['./incident.component.scss'],
  providers: [{ provide: NgbDateAdapter, useClass: NgbDateNativeAdapter }, I18n,
  { provide: NgbDatepickerI18n, useClass: CustomDatepickerI18n }]
})
export class IncidentComponent implements OnInit {
  @ViewChild('dialog') dialog: DialogComponent;
  cats = [
    {
      value: 'BLACK',
      viewValue: '违规事件',
    },
    {
      value: 'RED',
      viewValue: '表彰事件',
    }
  ];
  isAdd: boolean;
  incident: Incident;
  maxDate: any;
  domainId: number;
  constructor(private route: ActivatedRoute, private _location: Location, private incidentSrv: IncidentService) {
    this.incident = new Incident();
    this.incident.category = 'BLACK';
    this.incident.severity = 5;
    this.incident.happenTime = new Date();
    this.incident.subject = new Worker();
    const now = new Date();
    this.maxDate = {
      'year': now.getFullYear(),
      'month': now.getMonth() + 1,
      'day': now.getDate()
    };
  }

  ngOnInit() {
    this.domainId = +this.route.snapshot.paramMap.get('domainId');
    const id = +this.route.snapshot.paramMap.get('id');
    if (Number.isNaN(id)) {
      this.isAdd = true;
      return;
    }
    this.isAdd = false;
    this.incidentSrv.getIncident(this.domainId, id).subscribe(
      incident => {
        this.incident = incident;
      }
    );
  }

  onSubmit(): void {
    if (this.isAdd) {
      this.dialog.doWork('添加事件',
        this.incidentSrv.addIncident(this.domainId, this.incident));
    } else {
      // update incident
      this.dialog.doWork('修改事件',
        this.incidentSrv.updateIncident(this.domainId, this.incident));
    }
  }

}
