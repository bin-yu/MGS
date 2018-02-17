import { Injectable, OnInit } from '@angular/core';
import { Message } from './message';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { OnDestroy } from '@angular/core/src/metadata/lifecycle_hooks';


@Injectable()
export class MessageService {
  messages: Set<Message>;
  intervalId: any;

  constructor() {
    this.messages = new Set<Message>();

  }
  start() {
    this.intervalId = setInterval(() => {
      this.removeAgedMessage();
    }, 3000);
  }
  stop() {
    clearInterval(this.intervalId);
  }

  addSuccess(msg: string) {
    this.messages.add(new Message('success', msg));
  }
  addFail(msg: string) {
    this.messages.add(new Message('danger', msg));
  }
  add(message: Message) {
    this.messages.add(message);
  }
  remove(message: Message) {
    this.messages.delete(message);
  }
  clear() {
    this.messages.clear();
  }
  private removeAgedMessage() {
    this.messages.forEach(msg => {
      if (msg.isExpired()) {
        this.remove(msg);
      }
    });
  }
}
