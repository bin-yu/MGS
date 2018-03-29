
import { Injectable, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';

import {
    HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse
} from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError, map, tap } from 'rxjs/operators';
import { ErrorObservable } from 'rxjs/observable/ErrorObservable';

/** Pass untouched request through to the next request handler. */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor( @Inject(DOCUMENT) private document: any) {

    }
    intercept(req: HttpRequest<any>, next: HttpHandler):
        Observable<HttpEvent<any>> {
        const rt = next.handle(req);
        return rt.pipe(
            tap(
                resp => console.log(req.method + ':' + req.url + ', resp:' + JSON.stringify(resp))
            ),
            catchError(this.handleError<any>(req.url, null))
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

            // TODO: send the error to remote logging infrastructure
            console.error(JSON.stringify(error)); // log to console instead
            if (error.status === 401) {
                console.log('Unauthenticated! redirect to login');
                this.document.location.href = this.document.baseURI + 'login';
            }
            // Let the app keep running by returning an empty result.
            return new ErrorObservable('');
        };
    }
}
