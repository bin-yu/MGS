import { Pageable } from './../pageable';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { Worker } from './worker';
import { BackendService } from '../backend.service';
import { PagedResp } from '../paged-resp';
@Injectable()
export class WorkerService {

  constructor(private backend: BackendService) { }
  getWorkers(): Observable<Worker[]> {
    return this.backend.list<Worker>('/workers');
  }
  getWorkersx(pageable: Pageable): Observable<PagedResp<Worker>> {
    return this.backend.listx<Worker>('/workers', pageable);
  }
  getWorker(id: Number): Observable<Worker> {
    return this.backend.get<Worker>('/workers/' + id);
  }
  addWorker(worker: Worker): Observable<Worker> {
    return this.backend.post<Worker, Worker>('/workers', worker);
  }
  updateWorker(worker: Worker): Observable<Worker> {
    return this.backend.put<Worker>('/workers/' + worker.id, worker);
  }
  deleteWorker(worker: Worker): Observable<void> {
    return this.backend.delete<void>('/workers/' + worker.id);
  }
  findWorkers(nameLike: string): Observable<Worker[]> {
    return this.backend.getx<Worker[]>('/workers/search', {
      params: {
        nameLike: '%' + nameLike + '%'
      }
    });
  }
}
