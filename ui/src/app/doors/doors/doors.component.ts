import { MessageService } from './../../messages/messages.module';
import { DoorService, Door } from './../../backend/backend.module';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forEach } from '@angular/router/src/utils/collection';
import { PageableComponent } from '../../share/share.module';

@Component({
  selector: 'app-doors',
  templateUrl: './doors.component.html',
  styleUrls: ['./doors.component.scss']
})
export class DoorsComponent extends PageableComponent implements OnInit {
  doors: Door[];
  selectedDoors: Set<Door>;
  msg: string;
  constructor(private router: Router, private doorSrv: DoorService, private msgSrv: MessageService) {
    super();
    this.selectedDoors = new Set<Door>();
  }

  ngOnInit() {
    this.reloadItems();
  }
  reloadItems(): void {
    this.doorSrv.getDoorsx(this.pageable).subscribe(
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
    this.selectedDoors.forEach(
      (value: Door, value2: Door, set: Set<Door>) => {
        this.doorSrv.deleteDoor(value).subscribe(
          _ => {
            console.log('door deleted: ' + value.sn);
            this.msgSrv.addSuccess('门禁删除成功：' + value.sn);
            this.reloadItems();
          }
        );
      }
    );
  }

  performTest(): void {
    this.msg = '';
    this.selectedDoors.forEach(
      (value: Door, value2: Door, set: Set<Door>) => {
        this.doorSrv.getVersion(value).subscribe(
          version => {
            console.log('door ' + value.sn + ', version is ' + version);
            this.msg += '门禁 ' + value.sn + ' 版本 ：' + version + '</p>';
          }
        );
      }
    );
  }

  checkCardAreaStatus(): void {
    this.msg = '';
    this.selectedDoors.forEach(
      (value: Door, value2: Door, set: Set<Door>) => {
        this.doorSrv.getCardAreaStatus(value).subscribe(
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
        this.doorSrv.delCard(value.id, cardNo).subscribe(
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
        this.doorSrv.readCardData(value, cardNo).subscribe(
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
        this.doorSrv.addCardToBlackList(value, cardNo).subscribe(
          version => {
            console.log('door ' + value.sn + ': card added to blacklist: ' + cardNo);
            this.msgSrv.addSuccess('门禁 ' + value.sn + ': 卡已加入黑名单 ：' + cardNo);
          }
        );
      }
    );
  }
}
