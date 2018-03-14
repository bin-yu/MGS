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
  getDoors(domainId: number): Observable<Door[]> {
    return this.backend.list<Door>('/domains/' + domainId + '/doors');
  }
  getDoorsx(domainId: number, pageable: Pageable): Observable<PagedResp<Door>> {
    return this.backend.listx<Door>('/domains/' + domainId + '/doors', pageable);
  }
  getDoor(domainId: number, id: Number): Observable<Door> {
    return this.backend.get<Door>('/domains/' + domainId + '/doors/' + id);
  }
  addDoor(domainId: number, door: Door): Observable<Door> {
    return this.backend.post<Door, Door>('/domains/' + domainId + '/doors', door);
  }
  updateDoor(domainId: number, door: Door): Observable<Door> {
    return this.backend.put<Door>('/domains/' + domainId + '/doors/' + door.id, door);
  }
  deleteDoor(domainId: number, door: Door): Observable<void> {
    return this.backend.delete<void>('/domains/' + domainId + '/doors/' + door.id);
  }

  getVersion(domainId: number, door: Door): Observable<string> {
    return this.backend.get<Version>('/domains/' + domainId + '/doors/' + door.id + '/version').pipe(
      map(version => version.version)
    );
  }
  getCardAreaStatus(domainId: number, door: Door): Observable<CardAreaStatus> {
    return this.backend.get<CardAreaStatus>('/domains/' + domainId + '/doors/' + door.id + '/cardAreaStatus');
  }

  addCardToBlackList(domainId: number, door: Door, cardNo: number): Observable<void> {
    return this.backend.post<void, void>('/domains/' + domainId + '/doors/' + door.id + '/cards/' + cardNo + '/to-black-list', null);
  }

  readCardData(domainId: number, door: Door, cardNo: number): Observable<CardData> {
    return this.backend.get<CardData>('/domains/' + domainId + '/doors/' + door.id + '/cards/' + cardNo + '/cardData');
  }

  getCards(domainId: number, doorId: number): Observable<Card[]> {
    return this.backend.list<Card>('/domains/' + domainId + '/doors/' + doorId + '/cards');
  }
  getCardsx(domainId: number, doorId: number, pageable: Pageable): Observable<PagedResp<Card>> {
    return this.backend.listx<Card>('/domains/' + domainId + '/doors/' + doorId + '/cards', pageable);
  }
  getCard(domainId: number, doorId: number, cardNo: number): Observable<Card> {
    return this.backend.get<Card>('/domains/' + domainId + '/doors/' + doorId + '/cards/' + cardNo);
  }

  addCard(domainId: number, doorId: number, card: Card, isUpload: boolean): Observable<Card> {
    return this.backend.postx<void>('/domains/' + domainId + '/doors/' + doorId + '/cards', null, {
      params: {
        cid: card.cardNo,
        workerId: card.worker.id,
        upload: isUpload
      }
    });
  }
  delCard(domainId: number, doorId: number, cardNo: number): Observable<void> {
    return this.backend.delete<void>('/domains/' + domainId + '/doors/' + doorId + '/cards/' + cardNo);
  }
}
