import { DoorService } from './../../backend/door/door.service';
import { ActivatedRoute } from '@angular/router';
import { Component, OnInit, ViewChild, OnChanges, Output, EventEmitter, Input } from '@angular/core';
import { jqxTreeComponent } from 'jqwidgets-scripts/jqwidgets-ts/angular_jqxtree';

const DOOR_ICON = '/assets/img/door-icon.png';
@Component({
  selector: 'app-door-tree-2',
  templateUrl: './door-tree-2.component.html',
  styleUrls: ['./door-tree-2.component.scss']
})
export class DoorTree2Component implements OnChanges {


  @ViewChild('treeReference') tree: jqxTreeComponent;

  @Output()
  selectedChange = new EventEmitter<any>();
  _domainId: number;
  _selectedDooor: number;

  treeSource: any[] =
    [
      {
        icon: '/assets/img/folder-icon.png', label: '所有门', expanded: true,
        items:
          [
          ]
      }
    ];
  constructor(private route: ActivatedRoute, private doorSrv: DoorService) { }
  ngOnChanges(event: any) {
    if (!event.domainId) {
      return;
    }
    if (!this.domainId || isNaN(this.domainId)) {
      return;
    }
    this.doorSrv.getDoors(this.domainId).subscribe(
      doors => {
        let selectFirst = (this.selected > 0) ? false : true;
        this.tree.clear();
        doors.forEach(door => {
          const child: any = door;
          child.icon = DOOR_ICON;
          child.label = (child.label == null) ? child.sn : child.label;
          if (selectFirst || child.id === this.selected) {
            child.selected = true;
            selectFirst = false;
          }
          this.tree.addTo(child, null);
        });
      }
    );
  }
  onActivate(event: any): void {
    const item: any = this.tree.getSelectedItem();
    this.selected = item.id;
  }
  set selected(val: any) {
    const doorId = +val;
    if (doorId && (this._selectedDooor !== doorId)) {
      this._selectedDooor = doorId;
      this.selectedChange.emit(doorId);
    }
  }
  @Input()
  get selected() {
    return this._selectedDooor;
  }
  @Input()
  set domainId(val) {
    const id = +val;
    if (this._domainId !== id) {
      this._domainId = id;
      this._selectedDooor = undefined;
    }
  }
  get domainId() {
    return this._domainId;
  }
}
