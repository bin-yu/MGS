import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageService } from './message.service';
import { MessagesComponent } from './messages.component';

export { Message } from './message';
export { MessageService } from './message.service';
export { MessagesComponent } from './messages.component';
@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [MessagesComponent],
  exports: [MessagesComponent],
  providers: [MessageService]
})
export class MessagesModule { }
