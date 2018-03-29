import { DialogComponent } from './../../share/dialog/dialog.component';
import { Router } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { PageableComponent } from '../../share/pageable-component';
import { Policy, PolicyService, Action, Condition, PropertyName, Comparator } from '../../backend/backend.module';
import { MessageService } from '../../messages/message.service';

@Component({
  selector: 'app-policies',
  templateUrl: './policies.component.html',
  styleUrls: ['./policies.component.scss']
})
export class PoliciesComponent extends PageableComponent implements OnInit {
  @ViewChild('dialog') dialog: DialogComponent;
  policies: Policy[];
  selectedPolicys: Set<Policy>;
  constructor(private router: Router, private policySrv: PolicyService, private msgSrv: MessageService) {
    super();
    this.selectedPolicys = new Set<Policy>();
  }

  ngOnInit() {
    this.reloadItems();
  }
  reloadItems(): void {
    this.policySrv.getPoliciesx(this.pageable).toPromise().then(
      resp => {
        this.totalItems = resp.totalElements;
        this.policies = resp.content;
      }
    );
    this.selectedPolicys.clear();
  }
  handleSelectEvent(e, policy: Policy) {
    if (e.target.checked) {
      this.selectedPolicys.add(policy);
    } else {
      this.selectedPolicys.delete(policy);
    }
  }
  performDelete(): void {
    const obs = [];
    this.selectedPolicys.forEach(
      (value: Policy, value2: Policy, set: Set<Policy>) => {
        obs.push(this.policySrv.deletePolicy(value).map(
          _ => {
            console.log('policy deleted: ' + value.id);
          }
        ));
      }
    );
    this.dialog.title = '删除策略';
    this.dialog.startWorks('删除策略', obs).subscribe(
      _ => {
        this.reloadItems();
      }
    );
  }
  runAll(): void {
    this.dialog.doWork('运行所有策略',
      this.policySrv.runAll());
  }

}
