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
}
