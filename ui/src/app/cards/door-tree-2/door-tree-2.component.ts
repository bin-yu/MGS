import { DoorService } from './../../backend/door/door.service';
import { ActivatedRoute } from '@angular/router';
import { Component, OnInit, ViewChild, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { jqxTreeComponent } from 'jqwidgets-scripts/jqwidgets-ts/angular_jqxtree';
const DOOR_ICON = '/assets/img/door-icon.png';
@Component({
  selector: 'app-door-tree-2',
  templateUrl: './door-tree-2.component.html',
  styleUrls: ['./door-tree-2.component.scss']
})
export class DoorTree2Component implements AfterViewInit, OnInit {


  @ViewChild('treeReference') tree: jqxTreeComponent;

  @Output()
  onSelected = new EventEmitter<any>();

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
  ngOnInit() {
    this.doorSrv.getDoors().subscribe(
      doors => {
        const doorId = +this.route.firstChild.snapshot.paramMap.get('doorId');
        let selectFirst = (doorId > 0) ? false : true;
        const items = this.tree.getItems();
        doors.forEach(door => {
          const child: any = door;
          child.icon = DOOR_ICON;
          child.label = child.sn;
          if (selectFirst || child.id === doorId) {
            child.selected = true;
            selectFirst = false;
          }
          this.tree.addTo(child, items[0].element);
        });
      }
    );
  }
  ngAfterViewInit(): void {

  }
  onActivate(event: any): void {
    const item: any = this.tree.getItem(event.args.element);
    if (item.level === 0) {
      if (item.isExpanded) {
        this.tree.collapseItem(item);
      } else {
        this.tree.expandItem(item);
      }
    } else {
      this.onSelected.emit(item);
    }
  }
}
