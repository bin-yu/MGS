import { ActivatedRoute } from '@angular/router';
import { Component, OnInit, EventEmitter, Input, Output, ViewChild, AfterViewInit } from '@angular/core';
import { Door, DoorService } from '../../backend/backend.module';
import { TreeModel } from 'ng2-tree';

@Component({
  selector: 'app-door-tree',
  templateUrl: './door-tree.component.html',
  styleUrls: ['./door-tree.component.scss']
})
export class DoorTreeComponent implements AfterViewInit {
  @Output()
  onSelected = new EventEmitter<any>();

  @ViewChild('treeComponent') treeComponent;

  public root: TreeModel = {
    id: 1,
    value: '所有门',
    settings: {
      'selectionAllowed': false,
      'static': true,
      'rightMenu': false,
      'leftMenu': false,
      'cssClasses': {
        'expanded': 'fa fa-caret-down fa-lg',
        'collapsed': 'fa fa-caret-right fa-lg',
        'leaf': 'fa fa-lg',
        'empty': 'fa fa-caret-right disabled'
      },
      'templates': {
        'node': '<i class="fa far fa-folder fa-lg"  style="color:blue"></i>',
        'leaf': '<i class="fa fas fa-microchip fa-lg"></i>',
        'leftMenu': '<i class="fa fa-navicon fa-lg"></i>'
      }
    },
    loadChildren: callback => {
      this.doorSrv.getDoors().subscribe(
        doors => {
          const children = [];
          doors.forEach(door => {
            const child: any = door;
            child.value = child.sn;
            children.push(child);
          });
          callback(children);
        }
      );
    }
  };
  options = {};
  doorId;
  constructor(private route: ActivatedRoute, private doorSrv: DoorService) {
  }

  ngAfterViewInit() {
    const oopNodeController = this.treeComponent.getControllerByNodeId(1);
    oopNodeController.expand();
  }

  onActivate(event: any): void {
    this.onSelected.emit(event.node.node);
  }
  handleExpanded(event): void {
    /* this.doorId = +this.route.firstChild.snapshot.paramMap.get('doorId');
    if (this.doorId && this.doorId >= 0) {
      const oopNodeController = this.treeComponent.getControllerByNodeId(this.doorId);
      //if (oopNodeController) {
        oopNodeController.select();
        this.doorId = -1;
      //}
    } */
  }
}
