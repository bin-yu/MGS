import { ShareModule } from './../share/share.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { TreeModule } from 'ng2-tree';
import { CardsComponent } from './cards/cards.component';
import { CardsLayoutComponent } from './cards-layout/cards-layout.component';
import { DoorTreeComponent } from './door-tree/door-tree.component';
import { CardComponent } from './card/card.component';
import { CardRightLayoutComponent } from './card-right-layout/card-right-layout.component';
import { FormsModule } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { DoorTree2Component } from './door-tree-2/door-tree-2.component';
import { jqxTreeComponent } from 'jqwidgets-scripts/jqwidgets-ts/angular_jqxtree';

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
        path: ':doorId',
        component: CardsComponent
      },
      { path: ':doorId/:cardNo', component: CardComponent }
    ]
  }
];
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(CardRoutes),
    TreeModule,
    ShareModule,
    NgbTypeaheadModule
  ],
  declarations: [jqxTreeComponent,
    CardsComponent, CardsLayoutComponent, DoorTreeComponent, CardComponent, CardRightLayoutComponent, DoorTree2Component]
})
export class CardsModule { }
