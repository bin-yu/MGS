import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit, Input } from '@angular/core';
import { Door } from '../../backend/backend.module';

@Component({
  selector: 'app-card-right-layout',
  templateUrl: './card-right-layout.component.html',
  styleUrls: ['./card-right-layout.component.scss']
})
export class CardRightLayoutComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
  }

  @Input()
  set door(door: Door) {
    let url: string;
    url = './';
    if (door) {
      url += door.id;
    }
    this.router.navigate([url], { relativeTo: this.route });
  }
}
