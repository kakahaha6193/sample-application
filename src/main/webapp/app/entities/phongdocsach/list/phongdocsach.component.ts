import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPhongdocsach } from '../phongdocsach.model';
import { PhongdocsachService } from '../service/phongdocsach.service';
import { PhongdocsachDeleteDialogComponent } from '../delete/phongdocsach-delete-dialog.component';

@Component({
  selector: 'jhi-phongdocsach',
  templateUrl: './phongdocsach.component.html',
})
export class PhongdocsachComponent implements OnInit {
  phongdocsaches?: IPhongdocsach[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected phongdocsachService: PhongdocsachService,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.phongdocsachService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IPhongdocsach[]>) => {
            this.isLoading = false;
            this.phongdocsaches = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.phongdocsachService.query().subscribe(
      (res: HttpResponse<IPhongdocsach[]>) => {
        this.isLoading = false;
        this.phongdocsaches = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IPhongdocsach): number {
    return item.id!;
  }

  delete(phongdocsach: IPhongdocsach): void {
    const modalRef = this.modalService.open(PhongdocsachDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.phongdocsach = phongdocsach;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
