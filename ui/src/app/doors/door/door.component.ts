import { DialogComponent } from './../../share/dialog/dialog.component';
import { Location } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { DoorService, Door } from './../../backend/backend.module';

@Component({
  selector: 'app-door',
  templateUrl: './door.component.html',
  styleUrls: ['./door.component.scss']
})
export class DoorComponent implements OnInit {
  @ViewChild('dialog') dialog: DialogComponent;
  domainId: number;
  isAdd: boolean;
  door: Door;
  constructor(private route: ActivatedRoute, private _location: Location, private doorSrv: DoorService) {
    this.door = new Door();
    this.door.password = 'FFFFFFFF';
    this.door.port = 8000;
    this.door.protocol = 'TCP';
  }

  ngOnInit() {
    this.domainId = +this.route.snapshot.paramMap.get('domainId');
    const id = +this.route.snapshot.paramMap.get('id');
    if (Number.isNaN(id)) {
      this.isAdd = true;
      return;
    }
    this.isAdd = false;
    this.doorSrv.getDoor(this.domainId, id).subscribe(
      door => {
        this.door = door;
      }
    );
  }

  onSubmit(): void {
    if (this.isAdd) {
      this.dialog.doWork('添加门禁',
        this.doorSrv.addDoor(this.domainId, this.door));
    } else {
      // update door
      this.dialog.doWork('修改门禁',
        this.doorSrv.updateDoor(this.domainId, this.door));
    }
  }

}
