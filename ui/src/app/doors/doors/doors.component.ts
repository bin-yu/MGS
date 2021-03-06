import { DialogComponent } from './../../share/dialog/dialog.component';
import { MessageService } from './../../messages/messages.module';
import { DoorService, Door } from './../../backend/backend.module';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { forEach } from '@angular/router/src/utils/collection';
import { PageableComponent } from '../../share/share.module';

@Component({
  selector: 'app-doors',
  templateUrl: './doors.component.html',
  styleUrls: ['./doors.component.scss']
})
export class DoorsComponent extends PageableComponent implements OnInit {
  @ViewChild('dialog') dialog: DialogComponent;
  domainId: number;
  doors: Door[];
  selectedDoors: Set<Door>;
  msg: string;
  constructor(private route: ActivatedRoute, private router: Router, private doorSrv: DoorService, private msgSrv: MessageService) {
    super();
    this.selectedDoors = new Set<Door>();
    route.params.subscribe(
      val => {
        const domainId = + val.domainId;
        if (domainId && !isNaN(domainId)) {
          this.domainId = domainId;
          this.reloadItems();
        }
      }
    );
  }

  ngOnInit() {
  }
  reloadItems(): void {
    this.doorSrv.getDoorsx(this.domainId, this.pageable).subscribe(
      resp => {
        this.totalItems = resp.totalElements;
        this.doors = resp.content;
      }
    );
    this.selectedDoors.clear();
  }
  handleSelectEvent(e, door: Door) {
    if (e.target.checked) {
      this.selectedDoors.add(door);
    } else {
      this.selectedDoors.delete(door);
    }
  }

  performDelete(): void {
    const obs = [];
    this.selectedDoors.forEach(
      (value: Door, value2: Door, set: Set<Door>) => {
        obs.push(this.doorSrv.deleteDoor(this.domainId, value).map(
          _ => {
            console.log('door deleted: ' + value.sn);
          }
        ));
      }
    );

    this.dialog.title = '删除门禁';
    this.dialog.startWorks('删除门禁', obs).subscribe(
      _ => {
        this.reloadItems();
      }
    );
  }

  performTest(): void {
    const obs = [];
    this.selectedDoors.forEach(
      (value: Door, value2: Door, set: Set<Door>) => {
        obs.push(this.doorSrv.getVersion(this.domainId, value).map(
          version => {
            console.log('door ' + value.sn + ', version is ' + version);
          }
        ));
      }
    );
    this.dialog.title = '门禁连接测试';
    this.dialog.startWorks('门禁连接测试', obs).subscribe();
  }

  checkCardAreaStatus(): void {
    this.msg = '';
    this.selectedDoors.forEach(
      (value: Door, value2: Door, set: Set<Door>) => {
        this.doorSrv.getCardAreaStatus(this.domainId, value).subscribe(
          status => {
            console.log('door ' + value.sn + ', card area status: ' + JSON.stringify(status));
            this.msg += '门禁 ' + value.sn + ' 卡区域状态 ：' + JSON.stringify(status);
          }
        );
      }
    );
  }

  deleteCard(): void {
    this.msg = '';
    const cardNo = +prompt('请输入卡号：', '1');
    this.selectedDoors.forEach(
      (value: Door, value2: Door, set: Set<Door>) => {
        this.doorSrv.delCard(this.domainId, value.id, cardNo).subscribe(
          _ => {
            console.log('door ' + value.sn + ': card deleted: ' + cardNo);
            this.msgSrv.addSuccess('门禁 ' + value.sn + ': 卡已删除 ：' + cardNo);
          }
        );
      }
    );
  }

  readCardData(): void {
    this.msg = '';
    const cardNo = +prompt('请输入卡号：', '1');
    this.selectedDoors.forEach(
      (value: Door, value2: Door, set: Set<Door>) => {
        this.doorSrv.readCardData(this.domainId, value, cardNo).subscribe(
          data => {
            console.log('door ' + value.sn + ': card data: ' + JSON.stringify(data));
            this.msgSrv.addSuccess('门禁 ' + value.sn + ': 卡数据 ：' + JSON.stringify(data));
          }
        );
      }
    );
  }

  addCardToBlacklist(): void {
    this.msg = '';
    const cardNo = +prompt('请输入卡号：', '1');
    this.selectedDoors.forEach(
      (value: Door, value2: Door, set: Set<Door>) => {
        this.doorSrv.addCardToBlackList(this.domainId, value, cardNo).subscribe(
          version => {
            console.log('door ' + value.sn + ': card added to blacklist: ' + cardNo);
            this.msgSrv.addSuccess('门禁 ' + value.sn + ': 卡已加入黑名单 ：' + cardNo);
          }
        );
      }
    );
  }
}
