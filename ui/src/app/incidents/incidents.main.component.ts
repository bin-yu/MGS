import { Router, ActivatedRoute } from '@angular/router';
import { Component } from '@angular/core';

@Component({
  selector: 'app-incidents-main',
  templateUrl: './incidents.main.component.html',
  styleUrls: ['./incidents.main.component.scss']
})
export class IncidentsMainComponent {
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
