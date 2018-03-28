export class Worker {
  id: number;
  name: string;
  idNo: string;
  idType: string;
  types: string;
  phoneNums: string[];
  employer: string;
  inBlackList: boolean;
  trainingState: string;
  score: number;
  sex = MALE;
  homeAddr: string;
  incidentCnts: Map<String, number>;
  static create(id: number, name: string): Worker {
    const w = new Worker;
    w.id = id;
    w.name = name;
    return w;
  }
  public static clone(copy): Worker {
    const p = Object.assign(new Worker(), copy);
    return p;
  }
  public copy(copy): void {
    Object.assign(this, copy);
  }
  public getTrainingState(): string {
    switch (this.trainingState) {
      case NOT_TRAINED: return '需要培训';
      case TRAINED:
        if (this.inBlackList) {
          return '需要再培训';
        }
        return '培训通过';
      case RETRAINED:
        if (this.inBlackList) {
          return '需要再培训';
        }
        return '再培训通过';
      default:
        return this.trainingState;
    }
  }
  public getSexStr(): string {
    if (!this.sex) {
      return '未知';
    } else {
      return Sex[this.sex];
    }
  }
}
export const NOT_TRAINED = 'NOT_TRAINED';
export const TRAINED = 'TRAINED';
export const RETRAINED = 'RETRAINED';
export const MALE = 'MALE';
export const FEMALE = 'FEMALE';
export enum Sex {
  MALE = '男',
  FEMALE = '女'
}
