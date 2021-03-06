import { ShareModule } from './../share/share.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { CardsComponent } from './cards/cards.component';
import { CardsLayoutComponent } from './cards-layout/cards-layout.component';
import { CardComponent } from './card/card.component';
import { CardRightLayoutComponent } from './card-right-layout/card-right-layout.component';
import { FormsModule } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { DoorTree2Component } from './door-tree-2/door-tree-2.component';
import { jqxTreeComponent } from 'jqwidgets-scripts/jqwidgets-ts/angular_jqxtree';
import { jqxMenuComponent } from 'jqwidgets-scripts/jqwidgets-ts/angular_jqxmenu';

const CardRoutes: Routes = [
  {
    path: '',
    component: CardsLayoutComponent,
    children: [
      {
        path: '',
        component: CardsComponent
      },
      {
        path: ':domainId',
        component: CardsComponent
      },
      {
        path: ':domainId/:doorId',
        component: CardsComponent
      },
      { path: ':domainId/:doorId/:cardNo', component: CardComponent }
    ]
  }
];
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(CardRoutes),
    ShareModule,
    NgbTypeaheadModule
  ],
  declarations: [jqxTreeComponent, jqxMenuComponent,
    CardsComponent, CardsLayoutComponent, CardComponent, CardRightLayoutComponent, DoorTree2Component]
})
export class CardsModule { }
