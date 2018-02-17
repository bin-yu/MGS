import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { DoorService, Door } from './../../backend/backend.module';

@Component({
  selector: 'app-door',
  templateUrl: './door.component.html',
  styleUrls: ['./door.component.scss']
})
export class DoorComponent implements OnInit {

  isAdd: boolean;
  door: Door;
  constructor(private route: ActivatedRoute, private _location: Location, private doorSrv: DoorService) {
    this.door = new Door();
    this.door.password = 'FFFFFFFF';
    this.door.port = 8000;
    this.door.protocol = 'TCP';
  }

  ngOnInit() {
    const id = +this.route.snapshot.paramMap.get('id');
    if (Number.isNaN(id)) {
      this.isAdd = true;
      return;
    }
    this.isAdd = false;
    this.doorSrv.getDoor(id).subscribe(
      door => {
        this.door = door;
      }
    );
  }

  onSubmit(): void {
    if (this.isAdd) {
      this.doorSrv.addDoor(this.door).subscribe(
        door => {
          this._location.back();
        }
      );
    } else {
      // update door
      this.doorSrv.updateDoor(this.door).subscribe(
        door => {
          this._location.back();
        }
      );
    }
  }

}
