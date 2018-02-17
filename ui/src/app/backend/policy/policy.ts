import { Condition } from './condition';

export class Policy {
    id: number;
    name: string;
    condition = new Condition;
    action = 'ADD_TO_BLACKLIST';

    public static clone(copy): Policy {
        const p = Object.assign(new Policy(), copy);
        p.condition = Object.assign(new Condition(), p.condition);
        return p;
    }
    public getActionStr(): string {
        return Action[this.action];
    }
}
export enum Action {
    ADD_TO_BLACKLIST = '添加到黑名单',
    MONITOR = '告警'
}
