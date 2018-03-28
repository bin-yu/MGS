import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { PagedResp } from './paged-resp';
import { Injectable, Inject } from '@angular/core';
import { Pageable } from './pageable';
import { MessageService } from '../messages/message.service';


@Injectable()
export class BackendService {
  constructor(protected http: HttpClient, private msgSrv: MessageService) { }

  list<T>(url: string): Observable<T[]> {
    return this.http.get<PagedResp<T>>(environment.apibaseurl + url).pipe(
      map(
        resp => resp.content
      ),
      catchError(this.handleError<T[]>('读取', []))
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
      catchError(this.handleError<any>('读取', []))
      );
  }
  get<T>(url: string): Observable<T> {
    return this.http.get<T>(environment.apibaseurl + url).pipe(
      catchError(this.handleError<T>('读取', null))
    );
  }
  getx<T>(url: string, option?): Observable<T> {
    return this.http.get<T>(environment.apibaseurl + url, option).pipe(
      catchError(this.handleError<any>('读取', null))
    );
  }
  post<T, R>(url: string, body: T): Observable<R> {
    return this.http.post<R>(environment.apibaseurl + url, body).pipe(
      tap(_ => { this.logSucceed('添加'); }),
      catchError(this.handleError<R>('添加', null))
    );
  }
  postx<T>(url: string, body: T, options: any): Observable<any> {
    return this.http.post<any>(environment.apibaseurl + url, body, options).pipe(
      tap(_ => { this.logSucceed('添加'); }),
      catchError(this.handleError<any>('添加', null))
    );
  }
  put<T>(url: string, body: T): Observable<T> {
    return this.http.put<T>(environment.apibaseurl + url, body).pipe(
      tap(_ => { this.logSucceed('修改'); }),
      catchError(this.handleError<T>('修改', null))
    );
  }
  delete<T>(url: string): Observable<void> {
    return this.http.delete<void>(environment.apibaseurl + url).pipe(
      tap(_ => { this.logSucceed('删除'); }),
      catchError(this.handleError<void>('删除', null))
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
      this.msgSrv.addFail(operation + '失败');
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
  private logSucceed(operation: string) {
    this.msgSrv.addSuccess(operation + '成功');
  }
}
