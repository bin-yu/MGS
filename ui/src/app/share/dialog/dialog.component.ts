import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs/Observable';
import { Component, OnInit, Input, AfterViewInit, Output, EventEmitter } from '@angular/core';

import { Location } from '@angular/common';
@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent implements OnInit, AfterViewInit {
  @Input()
  title: string;
  @Input()
  goBackOnSuccess = false;
  @Output() dialogClosed = new EventEmitter();
  completeMsg = '操作成功';
  failed = false;
  progress: number;
  constructor(private _location: Location) { }

  ngOnInit() {
    this.progress = 0;
  }
  ngAfterViewInit() {
    $('#myModal').on('hidden.bs.modal', _ => {
      this.handleDialogClose();
    });
  }
  handleDialogClose() {
    if (this.goBackOnSuccess && this.failed === false) {
      this._location.back();
    }
    this.reset();
    this.dialogClosed.emit();
  }
  addProgress(inc: number) {
    if (!this.isComplete()) {
      this.progress += inc;
    }
  }
  succeed(msg: string) {
    this.completeMsg = msg;
    this.progress = 100;
  }
  fail(msg: string) {
    this.completeMsg = msg;
    this.failed = true;
  }
  isComplete() {
    return this.progress === 100 || this.failed;
  }
  reset() {
    this.progress = 0;
    this.failed = false;
    this.completeMsg = '';
  }

  open() {
    $('#myModal').modal({ backdrop: 'static' });
  }
  public doWork<T>(action: string, obs: Observable<T>) {
    this.open();
    this.addProgress(10);
    obs.subscribe(
      worker => {
        this.succeed(action + '成功');
      },
      error => {
        this.fail(action + '失败');
      }
    );
    this.addProgress(50);
  }
  public startWorks<T>(action: string, obs: Observable<T>[]): Observable<void | T[]> {
    this.open();
    this.addProgress(10);
    const step = 90 / obs.length;
    obs = obs.map(ob => ob.pipe(tap(
      null, null,
      () => {
        this.addProgress(step);
      }
    )));
    return Observable.forkJoin(obs).pipe(tap(
      worker => {
        this.succeed(action + '成功');
      },
      error => {
        this.fail(action + '失败');
      }
    ));
  }
}
