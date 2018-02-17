import { Component, OnInit } from '@angular/core';
import { Worker, WorkerService } from '../../backend/backend.module';
import { PageableComponent } from '../../share/share.module';

@Component({
  selector: 'app-scores',
  templateUrl: './scores.component.html',
  styleUrls: ['./scores.component.scss']
})
export class ScoresComponent extends PageableComponent implements OnInit {

  workers: Worker[];

  constructor(private workerSrv: WorkerService) {
    super();
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
  }

}
