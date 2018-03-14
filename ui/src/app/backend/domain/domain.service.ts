import { map } from 'rxjs/operators';
import { PagedResp } from './../paged-resp';
import { Pageable } from './../pageable';
import { BackendService } from './../backend.service';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Domain } from './domain';

@Injectable()
export class DomainService {

  constructor(private backend: BackendService) { }
  getRootDomain(): Observable<Domain> {
    return this.backend.get<Domain>('/domains');
  }
  getDomain(id: number): Observable<Domain> {
    return this.backend.get<Domain>('/domains/' + id);
  }
  addChildDomain(parentId: number, domain: Domain): Observable<Domain> {
    domain = Domain.copy(domain);
    return this.backend.post<Domain, Domain>('/domains/' + parentId + '/children', domain);
  }
  updateDomain(domain: Domain): Observable<Domain> {
    domain = Domain.copy(domain);
    return this.backend.put<Domain>('/domains/' + domain.id, domain);
  }
  deleteDomain(domainId: number): Observable<void> {
    return this.backend.delete<void>('/domains/' + domainId);
  }
}
