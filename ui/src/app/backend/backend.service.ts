import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { PagedResp } from './paged-resp';
import { Injectable, Inject } from '@angular/core';
import { Pageable } from './pageable';


@Injectable()
export class BackendService {

  constructor(protected http: HttpClient) { }

  list<T>(url: string): Observable<T[]> {
    return this.http.get<PagedResp<T>>(environment.apibaseurl + url).pipe(
      map(
        resp => {
          console.log('httplist, resp:' + JSON.stringify(resp));
          return resp.content;
        }
      ),
      catchError(this.handleError<T[]>('httplist', []))
    );
  }
  listx<T>(url: string, pagable: Pageable): Observable<PagedResp<T>> {
    return this.http.get<PagedResp<T>>(environment.apibaseurl + url, {
      params: {
        page: '' + pagable.page,
        size: '' + pagable.size,
        sort: pagable.sort
      }
    }).pipe(
      catchError(this.handleError<any>('httplist', []))
    );
  }
  get<T>(url: string): Observable<T> {
    return this.http.get<T>(environment.apibaseurl + url).pipe(
      catchError(this.handleError<T>('httpget', null))
    );
  }
  getx<T>(url: string, option?): Observable<T> {
    return this.http.get<T>(environment.apibaseurl + url, option).pipe(
      catchError(this.handleError<any>('httpgetx', null))
    );
  }
  post<T, R>(url: string, body: T): Observable<R> {
    return this.http.post<R>(environment.apibaseurl + url, body).pipe(
      catchError(this.handleError<R>('httppost', null))
    );
  }
  postx<T>(url: string, body: T, options: any): Observable<any> {
    return this.http.post<any>(environment.apibaseurl + url, body, options).pipe(
      catchError(this.handleError<any>('httppost', null))
    );
  }
  put<T>(url: string, body: T): Observable<T> {
    return this.http.put<T>(environment.apibaseurl + url, body).pipe(
      catchError(this.handleError<T>('httpput', null))
    );
  }
  delete<T>(url: string): Observable<void> {
    return this.http.delete<void>(environment.apibaseurl + url).pipe(
      catchError(this.handleError<void>('httpdelete', null))
    );
  }
  /**
     * Handle Http operation that failed.
     * Let the app continue.
     * @param operation - name of the operation that failed
     * @param result - optional value to return as the observable result
     */
  protected /*  */handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
