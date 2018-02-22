import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkerInputComponent } from './worker-input.component';

describe('WorkerInputComponent', () => {
  let component: WorkerInputComponent;
  let fixture: ComponentFixture<WorkerInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkerInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkerInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
