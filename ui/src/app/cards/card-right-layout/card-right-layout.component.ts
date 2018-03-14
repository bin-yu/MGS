import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit, Input } from '@angular/core';
import { Door } from '../../backend/backend.module';

@Component({
  selector: 'app-card-right-layout',
  templateUrl: './card-right-layout.component.html',
  styleUrls: ['./card-right-layout.component.scss']
})
export class CardRightLayoutComponent implements OnInit {
  @Input()
  domainId: number;

  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
  }

  @Input()
  set door(doorId: number) {
    if (doorId) {
      this.router.navigate(['./' + this.domainId + '/' + doorId], { relativeTo: this.route });
    }
  }
}
