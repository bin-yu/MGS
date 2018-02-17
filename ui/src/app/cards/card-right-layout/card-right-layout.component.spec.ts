import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CardRightLayoutComponent } from './card-right-layout.component';

describe('CardRightLayoutComponent', () => {
  let component: CardRightLayoutComponent;
  let fixture: ComponentFixture<CardRightLayoutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CardRightLayoutComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CardRightLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
