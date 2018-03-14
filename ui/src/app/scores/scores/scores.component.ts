import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Worker, WorkerService } from '../../backend/backend.module';
import { PageableComponent } from '../../share/share.module';

@Component({
  selector: 'app-scores',
  templateUrl: './scores.component.html',
  styleUrls: ['./scores.component.scss']
})
export class ScoresComponent extends PageableComponent implements OnInit {
  domainId: number;
  workers: Worker[];

  searchStr: string;
  constructor(private route: ActivatedRoute, private workerSrv: WorkerService) {
    super();
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
  }
  performSearch(event: any): void {
    if (event.code === 'Enter') {
      this.searchStr = event.target.value;
      console.log('Searching workers with "' + this.searchStr + '"');
      this.reloadItems();
    }
  }

}
