import { Pageable } from './../../backend/pageable';
import { Observable } from 'rxjs/Observable';

import { of } from 'rxjs/observable/of';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/switchMap';
import 'rxjs/add/operator/merge';
import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { Worker, WorkerService } from '../../backend/backend.module';

@Component({
  selector: 'app-worker-input',
  templateUrl: './worker-input.component.html',
  styleUrls: ['./worker-input.component.scss']
})
export class WorkerInputComponent implements OnInit {

  _worker: Worker;
  @Output() selectedChange = new EventEmitter();
  pageable = new Pageable(0, 10, 'id');
  search: any;
  searching = false;
  searchFailed = false;
  hideSearchingWhenUnsubscribed = new Observable(() => () => this.searching = false);
  formatter = (value: Worker) => value.name;
  constructor(private workerSrv: WorkerService) {
    this.search = (text$: Observable<string>) =>
      text$
        .debounceTime(300)
        .distinctUntilChanged()
        .do(() => this.searching = true)
        .switchMap(term =>
          this.workerSrv.findWorkers(term, this.pageable)
            .do(() => this.searchFailed = false)
            .catch(() => {
              this.searchFailed = true;
              return of([]);
            }))
        .do(() => this.searching = false)
        .merge(this.hideSearchingWhenUnsubscribed);
  }

  ngOnInit() {
  }

  @Input()
  get selected() {
    return this._worker;
  }

  set selected(val) {
    this._worker = val;
    this.selectedChange.emit(this._worker);
  }
}
