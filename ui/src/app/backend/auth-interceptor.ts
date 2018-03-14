
import { Injectable, Inject } from '@angular/core';
import { MessageService } from '../messages/messages.module';
import { DOCUMENT } from '@angular/platform-browser';

import {
    HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse
} from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError, map, tap } from 'rxjs/operators';

/** Pass untouched request through to the next request handler. */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(protected msgSrv: MessageService, @Inject(DOCUMENT) private document: any) {

    }
    intercept(req: HttpRequest<any>, next: HttpHandler):
        Observable<HttpEvent<any>> {
        const rt = next.handle(req);
        return rt.pipe(
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
            } else if (error.status) {
                this.msgSrv.addFail(operation + ' request failed, reason :' + (error.error) ? error.error.message : error.message);
            }
            if (error.message) {
                console.log('http error:' + error.message);
            }
            // Let the app keep running by returning an empty result.
            return of(result as T);
        };
    }
}
