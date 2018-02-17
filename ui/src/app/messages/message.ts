export class Message {
    type: string;
    content: string;
    expire = -1;
    constructor(type: string, content: string) {
        this.type = type;
        this.content = content;
        if ('success' === this.type) {
            this.expire = new Date().getTime() + 5000;
        }
    }
    isExpired(): boolean {
        return (this.expire > 0) && (this.expire <= new Date().getTime());
    }
}
