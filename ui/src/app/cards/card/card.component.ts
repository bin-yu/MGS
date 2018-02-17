import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Worker, Card, DoorService } from '../../backend/backend.module';
import { Location } from '@angular/common';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {
  card: Card;
  isAdd: boolean;
  constructor(private route: ActivatedRoute, private _location: Location, private doorSrv: DoorService) {
    this.card = new Card();
    this.card.worker = new Worker();
  }

  ngOnInit() {
    this.card.doorId = +this.route.snapshot.paramMap.get('doorId');
    const id = +this.route.snapshot.paramMap.get('cardNo');
    if (Number.isNaN(id)) {
      this.isAdd = true;
      return;
    }
    this.isAdd = false;
    this.doorSrv.getCard(this.card.doorId, id).subscribe(
      card => {
        this.card = card;
      }
    );
  }

  onSubmit(): void {
    if (this.isAdd) {
      this.doorSrv.addCard(this.card.doorId, this.card).subscribe(
        card => {
          this._location.back();
        }
      );
    }
  }

}
