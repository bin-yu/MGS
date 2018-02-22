import { Observable } from 'rxjs/Observable';

import {of} from 'rxjs/observable/of';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/switchMap';
import 'rxjs/add/operator/merge';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Worker, Card, DoorService, WorkerService } from '../../backend/backend.module';
import { Location } from '@angular/common';




@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {
  card: Card;
  isAdd: boolean;
  isUpload = false;
  constructor(private route: ActivatedRoute, private _location: Location, private doorSrv: DoorService, private workerSrv:WorkerService) {
    this.card = new Card();
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
      this.doorSrv.addCard(this.card.doorId, this.card, this.isUpload).subscribe(
        card => {
          this._location.back();
        }
      );
    }
  }

}
