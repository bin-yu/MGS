export class User {
    id: number;
    name: string;
    displayName: string;
    password: string;
    role = ROLE_USER;
    public static clone(copy): User {
        const p = Object.assign(new User(), copy);
        return p;
    }
    public getRoleStr(): string {
        return Role[this.role];
    }
}
export const ROLE_ADMIN = 'ADMIN';
export const ROLE_USER = 'USER';
export enum Role {
    ADMIN = '系统管理员',
    USER = '施工方管理员'
}
