import { AuthService } from './../../backend/auth/auth.service';
import { Observable } from 'rxjs/Observable';
import { Component, OnInit } from '@angular/core';
import { WorkerService, Worker, Pageable } from '../../backend/backend.module';
import { Router, ActivatedRoute } from '@angular/router';
import { forEach } from '@angular/router/src/utils/collection';
import { MessageService } from '../../messages/messages.module';
import { PageableComponent } from '../../share/share.module';
import 'rxjs/add/observable/forkJoin';
@Component({
  selector: 'app-workers',
  templateUrl: './workers.component.html',
  styleUrls: ['./workers.component.scss']
})
export class WorkersComponent extends PageableComponent implements OnInit {
  domainId: number;
  workers: Worker[];
  selectedWorkers: Set<Worker>;
  searchStr: string;
  isAdmin: boolean;

  constructor(private route: ActivatedRoute, private router: Router,
    private workerSrv: WorkerService, private msgSrv: MessageService, authSrv: AuthService) {
    super();
    this.isAdmin = authSrv.isAdmin();
    this.selectedWorkers = new Set<Worker>();
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
      this.workerSrv.findWorkersx(this.domainId, this.searchStr, this.pageable).subscribe(
        resp => {
          this.totalItems = resp.totalElements;
          this.workers = resp.content;
        }
      );
    } else {
      this.workerSrv.getWorkersx(this.domainId, this.pageable).subscribe(
        resp => {
          this.totalItems = resp.totalElements;
          this.workers = resp.content;
        }
      );
    }
    this.selectedWorkers.clear();
  }
  handleSelectEvent(e, worker: Worker) {
    if (e.target.checked) {
      this.selectedWorkers.add(worker);
    } else {
      this.selectedWorkers.delete(worker);
    }
  }

  performDelete(): void {
    let obs: Observable<void>[];
    obs = [];

    this.selectedWorkers.forEach(
      (value: Worker, value2: Worker, set: Set<Worker>) => {
        obs.push(this.workerSrv.deleteWorker(this.domainId, value).map(
          _ => {
            console.log('worker deleted: ' + value.name);
            this.msgSrv.addSuccess('劳工删除成功：' + value.name);
          }
        ));
      }
    );
    Observable.forkJoin(obs).subscribe(
      results => {
        this.reloadItems();
      }
    );
  }
  performSearch(event: any): void {
    if (event.code === 'Enter') {
      this.searchStr = event.target.value;
      console.log('Searching workers with "' + this.searchStr + '"');
      this.reloadItems();
    }
  }
  passTraining(): void {
    this.selectedWorkers.forEach(
      (value: Worker, value2: Worker, set: Set<Worker>) => {
        this.workerSrv.passTraining(this.domainId, value).subscribe(
          updatedWorker => {
            console.log('worker passed training: ' + value.name);
            this.msgSrv.addSuccess('劳工通过培训：' + value.name);
            value.copy(updatedWorker);
          }
        );
      }
    );
  }
}
