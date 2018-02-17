export class CardData {
    private cardNo: number;
    private password: string;
    private expireTime: string;
    private timePieceIndex: string;
    private readCount: number;//有效次数
    private openLock: number;//权限
    private cardMode: number;//特权模式
    private state: string;//卡状态
    private HldPwr: string;//节假日
    private inputOutputState: number;//出入标志
    private lastReadTime: string;
}
