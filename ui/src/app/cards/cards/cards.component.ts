import { Component, OnInit, Input } from '@angular/core';
import { Card, Door, DoorService } from '../../backend/backend.module';
import { MessageService } from '../../messages/message.service';
import { ActivatedRoute } from '@angular/router';
import { PageableComponent } from '../../share/share.module';

@Component({
  selector: 'app-cards',
  templateUrl: './cards.component.html',
  styleUrls: ['./cards.component.scss']
})
export class CardsComponent extends PageableComponent implements OnInit {
  domainId: number;
  doorId: number;
  cards: Card[];
  selectedCards: Set<Card>;
  searchStr: string;

  constructor(private route: ActivatedRoute, private doorSrv: DoorService, private msgSrv: MessageService) {
    super();
    this.pageable.sort = 'cardNo,asc';
    this.pageable.size = 9;
    this.selectedCards = new Set<Card>();
    route.params.subscribe(
      val => {
        const doorId = + val.doorId;
        if (doorId && !isNaN(doorId)) {
          this.doorId = doorId;
        }
        const domainId = + val.domainId;
        if (domainId && !isNaN(domainId)) {
          this.domainId = domainId;
        }
        this.reloadItems();
      }
    );
  }
  ngOnInit() {
  }
  reloadItems(): void {
    this.cards = [];
    this.selectedCards.clear();
    if (!isNaN(this.doorId) && !isNaN(this.domainId)) {
      if (this.searchStr && this.searchStr.length > 0) {
        this.doorSrv.findCards(this.domainId, this.doorId, this.searchStr, this.pageable).subscribe(
          resp => {
            this.totalItems = resp.totalElements;
            this.cards = resp.content;
          }
        );
      } else {
        this.doorSrv.getCardsx(this.domainId, this.doorId, this.pageable).subscribe(
          resp => {
            this.totalItems = resp.totalElements;
            this.cards = resp.content;
          }
        );
      }
    }
  }
  handleSelectEvent(e, card: Card) {
    if (e.target.checked) {
      this.selectedCards.add(card);
    } else {
      this.selectedCards.delete(card);
    }
  }

  performDelete(): void {
    this.selectedCards.forEach(
      (value: Card, value2: Card, set: Set<Card>) => {
        this.doorSrv.delCard(this.domainId, this.doorId, value.cardNo).subscribe(
          _ => {
            console.log('card deleted: ' + value.cardNo);
            this.msgSrv.addSuccess('卡片删除成功：' + value.cardNo);
            this.reloadItems();
          }
        );
      }
    );
  }
  performSearch(event: any): void {
    if (event.code === 'Enter') {
      this.searchStr = event.target.value;
      console.log('Searching cards with worker name : "' + this.searchStr + '"');
      this.reloadItems();
    }
  }
}
