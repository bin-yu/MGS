import { Component, OnInit, EventEmitter, Input, Output } from '@angular/core';
import { Door, DoorService } from '../../backend/backend.module';

@Component({
  selector: 'app-door-tree',
  templateUrl: './door-tree.component.html',
  styleUrls: ['./door-tree.component.scss']
})
export class DoorTreeComponent implements OnInit {
  @Output()
  onSelected = new EventEmitter<any>();


  root: any[];
  options = {};
  constructor(private doorSrv: DoorService) {
  }

  ngOnInit() {
    this.reloadDoors();
  }
  reloadDoors(): void {
    this.doorSrv.getDoors().subscribe(
      doors => {
        this.root = [
          {
            id: 1,
            name: '所有门',
            isExpanded: true,
            children: doors
          }
        ];
        this.root[0].children.forEach(door => { door.name = door.sn; });
      }
    );
  }

  onActivate(event: any): void {
    this.onSelected.emit(event.node.data);
  }
}
