import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CardBlacklistComponent } from './card-blacklist.component';

describe('CardBlacklistComponent', () => {
  let component: CardBlacklistComponent;
  let fixture: ComponentFixture<CardBlacklistComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CardBlacklistComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CardBlacklistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
