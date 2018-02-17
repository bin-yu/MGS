export class User {
    id: number;
    name: string;
    displayName: string;
    password: string;
    role = 'ADMIN';
    public static clone(copy): User {
        const p = Object.assign(new User(), copy);
        return p;
    }
    public getRoleStr(): string {
        return Role[this.role];
    }
}
export enum Role {
    ADMIN = '管理员',
    USER = '普通用户'
}
