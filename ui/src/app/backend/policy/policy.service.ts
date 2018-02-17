import { map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { BackendService } from '../backend.service';
import { Observable } from 'rxjs/Observable';
import { Policy } from './policy';
import { Pageable } from '../pageable';
import { PagedResp } from '../paged-resp';

@Injectable()
export class PolicyService {

  constructor(private backend: BackendService) { }
  /* getPolicies(): Observable<Policy[]> {
    return this.backend.list<Policy>('/policies');
  } */
  getPoliciesx(pageable: Pageable): Observable<PagedResp<Policy>> {
    return this.backend.listx<Policy>('/policies', pageable).pipe(
      map(resp => {
        const policies = [];
        resp.content.forEach(p => { policies.push(Policy.clone(p)); });
        resp.content = policies;
        return resp;
      })
    );
  }
  getPolicy(id: Number): Observable<Policy> {
    return this.backend.get<Policy>('/policies/' + id).pipe(
      map(policy => Policy.clone(policy))
    );
  }
  addPolicy(policy: Policy): Observable<Policy> {
    return this.backend.post<Policy, Policy>('/policies', policy).pipe(
      map(p => Policy.clone(p))
    );
  }
  updatePolicy(policy: Policy): Observable<Policy> {
    return this.backend.put<Policy>('/policies/' + policy.id, policy).pipe(
      map(p => Policy.clone(p))
    );
  }
  deletePolicy(policy: Policy): Observable<void> {
    return this.backend.delete<void>('/policies/' + policy.id);
  }
  runAll(): Observable<void> {
    return this.backend.post<void, void>('/policies/runNow', null);
  }
}
