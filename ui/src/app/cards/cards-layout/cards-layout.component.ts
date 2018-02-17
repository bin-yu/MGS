import { Component, OnInit } from '@angular/core';
import { Door } from '../../backend/backend.module';

@Component({
  selector: 'app-cards-layout',
  templateUrl: './cards-layout.component.html',
  styleUrls: ['./cards-layout.component.scss']
})
export class CardsLayoutComponent implements OnInit {
  selectedDoor: Door;
  constructor() { }

  ngOnInit() {
  }

  onDoorSelected(door: Door): void {
    this.selectedDoor = door;
  }
}
