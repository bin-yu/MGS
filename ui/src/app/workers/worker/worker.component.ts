import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { WorkerService } from '../../backend/backend.module';
import { Worker } from '../../backend/backend.module';
import { Location } from '@angular/common';
@Component({
  selector: 'app-worker',
  templateUrl: './worker.component.html',
  styleUrls: ['./worker.component.scss']
})
export class WorkerComponent implements OnInit {
  isAdd: boolean;
  worker: Worker;
  constructor(private route: ActivatedRoute, private _location: Location, private workerSrv: WorkerService) {
    this.worker = new Worker();
    this.worker.idType = 'ID Card';
    this.worker.phoneNums = [''];
  }

  ngOnInit() {
    const id = +this.route.snapshot.paramMap.get('id');
    if (Number.isNaN(id)) {
      this.isAdd = true;
      return;
    }
    this.isAdd = false;
    this.workerSrv.getWorker(id).subscribe(
      worker => {
        this.worker = worker;
      }
    );
  }

  onSubmit(): void {
    if (this.isAdd) {
      this.workerSrv.addWorker(this.worker).subscribe(
        worker => {
          this._location.back();
        }
      );
    } else {
      // update worker
      this.workerSrv.updateWorker(this.worker).subscribe(
        worker => {
          this._location.back();
        }
      );
    }
  }
  onCancel(): void {
    this._location.back();
  }
}
