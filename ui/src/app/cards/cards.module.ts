import { ShareModule } from './../share/share.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { TreeModule } from 'angular-tree-component';
import { CardsComponent } from './cards/cards.component';
import { CardsLayoutComponent } from './cards-layout/cards-layout.component';
import { DoorTreeComponent } from './door-tree/door-tree.component';
import { CardComponent } from './card/card.component';
import { CardRightLayoutComponent } from './card-right-layout/card-right-layout.component';
import { FormsModule } from '@angular/forms';

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
    ShareModule
  ],
  declarations: [CardsComponent, CardsLayoutComponent, DoorTreeComponent, CardComponent, CardRightLayoutComponent]
})
export class CardsModule { }
