import { Router, ActivatedRoute } from '@angular/router';
import { Component } from '@angular/core';

@Component({
  selector: 'app-users-main',
  templateUrl: './users-main.component.html',
  styleUrls: ['./users-main.component.scss']
})
export class UsersMainComponent {
  selectedDomain: number;
  constructor(private route: ActivatedRoute, private router: Router) {
    route.firstChild.params.subscribe(
      val => {
        const domainId = + val.domainId;
        if (domainId && !isNaN(domainId)) {
          this.selectedDomain = domainId;
        }
      }
    );
  }
  onDomainChange(event: any): any {
    if (event) {
      console.log('Current domain changed to ' + event);
      this.router.navigate(['./' + event], { relativeTo: this.route });
    }
  }
}
