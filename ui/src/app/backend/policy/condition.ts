export class Condition {
    property = 'SCORE';
    comparator = 'SMALLER_THAN';
    value = 0;
    public toString(): string {
        return PropertyName[this.property] + ' ' + Comparator[this.comparator] + ' ' + this.value;
    }
}

export enum PropertyName {
    BLACK_INCIDENTS_CNT = '违规次数',
    SCORE = '分数'
}
export enum Comparator {
    EQUALS = '等于',
    LARGER_THAN = '大于',
    SMALLER_THAN = '小于'
}
