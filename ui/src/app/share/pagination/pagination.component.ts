import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss'],
  providers: [NgbPaginationConfig] // add NgbPaginationConfig to the component providers
})
export class PaginationComponent implements OnInit {
  @Input()
  totalItems: number;
  @Input()
  pageSize: number;
  @Output()
  onPageLoad = new EventEmitter<number>();
  currentPage: number;

  totalPages: number;

  constructor(private config: NgbPaginationConfig) {
    this.currentPage = 1;
    this.totalPages = this.totalItems / this.pageSize;
  }

  ngOnInit() {
    this.config.pageSize = 20;
    console.log('total:' + this.totalItems + ',pageSize:' + this.pageSize);
  }
  onPageChange(event: any): void {
    console.log('pageEvent:' + JSON.stringify(event));
    this.onPageLoad.emit(event);
  }
}
