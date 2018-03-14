import { map } from 'rxjs/operators';
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
  getWorkersx(domainId: number, pageable: Pageable): Observable<PagedResp<Worker>> {
    return this.backend.listx<Worker>('/domains/' + domainId + '/workers', pageable);
  }
  getWorker(domainId: number, id: Number): Observable<Worker> {
    return this.backend.get<Worker>('/domains/' + domainId + '/workers/' + id);
  }
  addWorker(domainId: number, worker: Worker): Observable<Worker> {
    return this.backend.post<Worker, Worker>('/domains/' + domainId + '/workers', worker);
  }
  updateWorker(domainId: number, worker: Worker): Observable<Worker> {
    return this.backend.put<Worker>('/domains/' + domainId + '/workers/' + worker.id, worker);
  }
  deleteWorker(domainId: number, worker: Worker): Observable<void> {
    return this.backend.delete<void>('/domains/' + domainId + '/workers/' + worker.id);
  }
  findWorkersx(domainId: number, nameLike: string, pageable: Pageable): Observable<PagedResp<Worker>> {
    return this.backend.getx<PagedResp<Worker>>('/domains/' + domainId + '/workers/search', {
      params: {
        nameLike: '%' + nameLike + '%',
        page: '' + pageable.page,
        size: '' + pageable.size,
        sort: pageable.sort
      }
    });
  }
  findWorkers(domainId: number, nameLike: string, pageable: Pageable): Observable<Worker[]> {
    return this.findWorkersx(domainId, nameLike, pageable).pipe(
      map(
        resp => resp.content
      )
    );
  }
}
