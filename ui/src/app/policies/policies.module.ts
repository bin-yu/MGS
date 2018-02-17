import { ShareModule } from './../share/share.module';
import { Routes, RouterModule } from '@angular/router';
import { PoliciesMainComponent } from './policies-main/policies-main.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PoliciesComponent } from './policies/policies.component';
import { PolicyComponent } from './policy/policy.component';

const PolRoutes: Routes = [
  {
    path: '',
    component: PoliciesMainComponent,
    children: [
      {
        path: '',
        component: PoliciesComponent
      },
      { path: ':id', component: PolicyComponent }
    ]
  }
];
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ShareModule,
    RouterModule.forChild(PolRoutes)
  ],
  declarations: [PoliciesMainComponent, PoliciesComponent, PolicyComponent]
})
export class PoliciesModule { }
