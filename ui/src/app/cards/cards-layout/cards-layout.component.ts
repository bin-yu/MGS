import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Door } from '../../backend/backend.module';

@Component({
  selector: 'app-cards-layout',
  templateUrl: './cards-layout.component.html',
  styleUrls: ['./cards-layout.component.scss']
})
export class CardsLayoutComponent implements OnInit {
  selectedDoor: string;
  selectedDomain: string;
  constructor(private route: ActivatedRoute, private router: Router) {
    route.firstChild.params.subscribe(
      val => {
        if (val.domainId && (this.selectedDomain !== val.domainId)) {
          this.selectedDomain = val.domainId;
        }
        if (val.doorId && (this.selectedDoor !== val.doorId)) {
          this.selectedDoor = val.doorId;
        }
      }
    );
  }

  ngOnInit() {
  }
  onSelectedDoorChange(event: any) {
    console.log('Selected door change to ' + event);
  }
}
