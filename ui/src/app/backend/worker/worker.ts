export class Worker {
  id: number;
  name: string;
  idNo: string;
  idType: string;
  types: string;
  phoneNums: string[];
  employer: string;
  inBlackList: boolean;
  score: number;
  incidentCnts: Map<String, number>;
  static create(id: number, name: string): Worker {
    const w = new Worker;
    w.id = id;
    w.name = name;
    return w;
  }
}
