import { Component, OnInit } from '@angular/core';
import { WorkerService } from '../backend/backend.module';
import { Worker, Pageable } from '../backend/backend.module';
import { Router } from '@angular/router';
import { forEach } from '@angular/router/src/utils/collection';
import { MessageService } from '../messages/messages.module';
import { PageableComponent } from '../share/share.module';
@Component({
  selector: 'app-workers',
  templateUrl: './workers.component.html',
  styleUrls: ['./workers.component.scss']
})
export class WorkersComponent extends PageableComponent implements OnInit {

  workers: Worker[];
  selectedWorkers: Set<Worker>;

  constructor(private router: Router, private workerSrv: WorkerService, private msgSrv: MessageService) {
    super();
    this.selectedWorkers = new Set<Worker>();
  }

  ngOnInit() {
    this.reloadItems();
  }
  reloadItems(): void {
    this.workerSrv.getWorkersx(this.pageable).subscribe(
      resp => {
        this.totalItems = resp.totalElements;
        this.workers = resp.content;
      }
    );
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
    this.selectedWorkers.forEach(
      (value: Worker, value2: Worker, set: Set<Worker>) => {
        this.workerSrv.deleteWorker(value).subscribe(
          _ => {
            console.log('worker deleted: ' + value.name);
            this.msgSrv.addSuccess('民工删除成功：' + value.name);
            this.reloadItems();
          }
        );
      }
    );
  }
}
