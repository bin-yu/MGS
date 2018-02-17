import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DoorTreeComponent } from './door-tree.component';

describe('DoorTreeComponent', () => {
  let component: DoorTreeComponent;
  let fixture: ComponentFixture<DoorTreeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DoorTreeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DoorTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
