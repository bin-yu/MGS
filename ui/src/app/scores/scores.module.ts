import { ScoresMainComponent } from './scores.main.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ScoresComponent } from './scores/scores.component';
import { ShareModule } from '../share/share.module';

const ScoreRoutes: Routes = [
  {
    path: '',
    component: ScoresMainComponent,
    children: [
      {
        path: '',
        component: ScoresComponent
      }
    ]
  }
];
@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(ScoreRoutes),
    ShareModule
  ],
  declarations: [ScoresMainComponent, ScoresComponent]
})
export class ScoresModule { }
