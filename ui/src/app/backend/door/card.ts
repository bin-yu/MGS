import { Worker } from '../worker/worker';
export class Card {
    doorId: number;
    cardNo: number;
    worker: Worker;
    inBlackList: boolean;
}
