import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { PolicyService } from '../../backend/policy/policy.service';
import { Policy, Condition, Action, PropertyName, Comparator } from '../../backend/backend.module';

@Component({
  selector: 'app-policy',
  templateUrl: './policy.component.html',
  styleUrls: ['./policy.component.scss']
})
export class PolicyComponent implements OnInit {

  policy: Policy;
  isAdd: boolean;
  actions = Object.entries(Action);
  properties = Object.entries(PropertyName);
  comparators = Object.entries(Comparator);
  constructor(private route: ActivatedRoute, private _location: Location, private srv: PolicyService) {
    this.policy = new Policy();
  }

  ngOnInit() {
    const id = +this.route.snapshot.paramMap.get('id');
    if (Number.isNaN(id)) {
      this.isAdd = true;
      return;
    }
    this.isAdd = false;
    this.srv.getPolicy(id).subscribe(
      policy => {
        this.policy = policy;
      }
    );
  }
  onSubmit(): void {
    if (this.isAdd) {
      this.srv.addPolicy(this.policy).subscribe(
        policy => {
          this._location.back();
        }
      );
    } else {
      this.srv.updatePolicy(this.policy).subscribe(
        policy => {
          this._location.back();
        }
      );
    }
  }

}
