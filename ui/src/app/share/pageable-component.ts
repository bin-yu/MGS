import { Pageable } from './../backend/backend.module';
export abstract class PageableComponent {
    totalItems = 0;
    pageable = new Pageable(0, 10, 'id,asc');
    loadPage(page: number): void {
        this.pageable.page = page - 1;
        this.reloadItems();
    }
    abstract reloadItems();
}
