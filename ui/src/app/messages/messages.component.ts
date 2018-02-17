import { OnDestroy } from '@angular/core/src/metadata/lifecycle_hooks';
import { Component, OnInit } from '@angular/core';
import { MessageService } from './message.service';
@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.css']
})
export class MessagesComponent implements OnInit, OnDestroy {

  constructor(public messageService: MessageService) { }

  ngOnInit() {
    this.messageService.start();
  }
  ngOnDestroy() {
    this.messageService.stop();
  }
}
