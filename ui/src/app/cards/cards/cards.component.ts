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
  doorId: number;
  cards: Card[];
  selectedCards: Set<Card>;

  constructor(private route: ActivatedRoute, private doorSrv: DoorService, private msgSrv: MessageService) {
    super();
    this.pageable.sort = 'cardNo,asc';
    this.pageable.size = 9;
    this.selectedCards = new Set<Card>();
    route.params.subscribe(
      val => {
        this.doorId = + val.doorId;
        this.reloadItems();
      }
    );
  }
 /*  @Input()
  set door(door: Door) {
    this._door = door;
    this.reloadCards();
  }
  get door(): Door { return this._door; } */

  ngOnInit() {
  }
  reloadItems(): void {
    this.cards = [];
    this.selectedCards.clear();
    if (! Number.isNaN(this.doorId)) {
      this.doorSrv.getCardsx(this.doorId, this.pageable).subscribe(
        resp => {
          this.totalItems = resp.totalElements;
          this.cards = resp.content;
        }
      );
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
        this.doorSrv.delCard(this.doorId, value.cardNo).subscribe(
          _ => {
            console.log('card deleted: ' + value.cardNo);
            this.msgSrv.addSuccess('卡片删除成功：' + value.cardNo);
            this.reloadItems();
          }
        );
      }
    );
  }

}
