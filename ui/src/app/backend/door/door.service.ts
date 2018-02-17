import { Pageable } from './../pageable';
import { CardData } from './cardData';
import { Version } from './version';
import { Door } from './door';
import { CardAreaStatus } from './cardAreaStatus';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { map, tap } from 'rxjs/operators';
import { BackendService } from '../backend.service';
import { Card } from './card';
import { PagedResp } from '../paged-resp';

@Injectable()
export class DoorService {

  constructor(private backend: BackendService) { }
  getDoors(): Observable<Door[]> {
    return this.backend.list<Door>('/doors');
  }
  getDoorsx(pageable: Pageable): Observable<PagedResp<Door>> {
    return this.backend.listx<Door>('/doors', pageable);
  }
  getDoor(id: Number): Observable<Door> {
    return this.backend.get<Door>('/doors/' + id);
  }
  addDoor(door: Door): Observable<Door> {
    return this.backend.post<Door, Door>('/doors', door);
  }
  updateDoor(door: Door): Observable<Door> {
    return this.backend.put<Door>('/doors/' + door.id, door);
  }
  deleteDoor(door: Door): Observable<void> {
    return this.backend.delete<void>('/doors/' + door.id);
  }

  getVersion(door: Door): Observable<string> {
    return this.backend.get<Version>('/doors/' + door.id + '/version').pipe(
      map(version => version.version)
    );
  }
  getCardAreaStatus(door: Door): Observable<CardAreaStatus> {
    return this.backend.get<CardAreaStatus>('/doors/' + door.id + '/cardAreaStatus');
  }

  delCard(doorId: number, cardNo: number): Observable<void> {
    return this.backend.delete<void>('/doors/' + doorId + '/cards/' + cardNo);
  }

  addCardToBlackList(door: Door, cardNo: number): Observable<void> {
    return this.backend.post<void, void>('/doors/' + door.id + '/cards/' + cardNo + '/to-black-list', null);
  }

  readCardData(door: Door, cardNo: number): Observable<CardData> {
    return this.backend.get<CardData>('/doors/' + door.id + '/cards/' + cardNo + '/cardData');
  }

  getCards(doorId: number): Observable<Card[]> {
    return this.backend.list<Card>('/doors/' + doorId + '/cards');
  }
  getCardsx(doorId: number, pageable: Pageable): Observable<PagedResp<Card>> {
    return this.backend.listx<Card>('/doors/' + doorId + '/cards', pageable);
  }
  getCard(doorId: number, cardNo: number): Observable<Card> {
    return this.backend.get<Card>('/doors/' + doorId + '/cards/' + cardNo);
  }

  addCard(doorId: number, card: Card, isUpload: boolean): Observable<Card> {
    return this.backend.postx<void>('/doors/' + doorId + '/cards', null, {
      params: {
        cid: card.cardNo,
        workerId: card.worker.id,
        upload: isUpload
      }
    });
  }
}
