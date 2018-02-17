import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { PageableComponent } from '../../share/pageable-component';
import { Policy, PolicyService, Action, Condition, PropertyName, Comparator } from '../../backend/backend.module';
import { MessageService } from '../../messages/message.service';

@Component({
  selector: 'app-policies',
  templateUrl: './policies.component.html',
  styleUrls: ['./policies.component.scss']
})
export class PoliciesComponent extends PageableComponent implements OnInit {

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
    this.selectedPolicys.forEach(
      (value: Policy, value2: Policy, set: Set<Policy>) => {
        this.policySrv.deletePolicy(value).subscribe(
          _ => {
            console.log('policy deleted: ' + value.id);
            this.msgSrv.addSuccess('策略删除成功：' + value.id);
            this.reloadItems();
          }
        );
      }
    );
  }
  runAll(): void {
    this.policySrv.runAll().subscribe(
      _ => {
        console.log('Run all policies now !!!');
        this.msgSrv.addSuccess('策略成功开始运行');
      }
    );
  }

}
