import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DoorTree2Component } from './door-tree-2.component';

describe('DoorTree2Component', () => {
  let component: DoorTree2Component;
  let fixture: ComponentFixture<DoorTree2Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DoorTree2Component ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DoorTree2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
