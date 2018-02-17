export class PagedResp<T> {
    content: T [];
    totalPages: number;
    totalElements: number;
    last: boolean;
    size: number;
    first: boolean;
    numberOfElements: number;
}