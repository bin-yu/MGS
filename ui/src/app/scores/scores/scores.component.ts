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

  searchStr: string;
  constructor(private workerSrv: WorkerService) {
    super();
  }

  ngOnInit() {
    this.reloadItems();
  }
  reloadItems(): void {
    if (this.searchStr && this.searchStr.length > 0) {
      this.workerSrv.findWorkersx(this.searchStr, this.pageable).subscribe(
        resp => {
          this.totalItems = resp.totalElements;
          this.workers = resp.content;
        }
      );
    } else {
      this.workerSrv.getWorkersx(this.pageable).subscribe(
        resp => {
          this.totalItems = resp.totalElements;
          this.workers = resp.content;
        }
      );
    }
  }
  performSearch(event: any): void {
    if (event.code === 'Enter') {
      this.searchStr = event.target.value;
      console.log('Searching workers with "' + this.searchStr + '"');
      this.reloadItems();
    }
  }

}
