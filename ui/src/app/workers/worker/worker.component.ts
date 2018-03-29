import { DialogComponent } from './../../share/dialog/dialog.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { WorkerService } from '../../backend/backend.module';
import { Worker, Sex, MALE, FEMALE } from '../../backend/backend.module';
import { Location } from '@angular/common';
@Component({
  selector: 'app-worker',
  templateUrl: './worker.component.html',
  styleUrls: ['./worker.component.scss']
})
export class WorkerComponent implements OnInit {
  @ViewChild('dialog') dialog: DialogComponent;
  domainId: number;
  isAdd: boolean;
  worker: Worker;
  sexes = Object.entries(Sex);
  constructor(private route: ActivatedRoute, private _location: Location, private workerSrv: WorkerService) {
    this.worker = new Worker();
    this.worker.idType = 'ID Card';
    this.worker.phoneNums = [''];
  }

  ngOnInit() {
    this.domainId = +this.route.snapshot.paramMap.get('domainId');
    const id = +this.route.snapshot.paramMap.get('id');
    if (Number.isNaN(id)) {
      this.isAdd = true;
      return;
    }
    this.isAdd = false;
    this.workerSrv.getWorker(this.domainId, id).subscribe(
      worker => {
        this.worker = worker;
      }
    );
  }

  onSubmit(): void {
    if (this.isAdd) {
      this.dialog.doWork('添加新劳工', this.workerSrv.addWorker(this.domainId, this.worker));
    } else {
      // update worker
      this.dialog.doWork('修改劳工', this.workerSrv.updateWorker(this.domainId, this.worker));
    }
  }
}
