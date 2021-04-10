import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICuonsach } from '../cuonsach.model';
import { CuonsachService } from '../service/cuonsach.service';
import { CuonsachDeleteDialogComponent } from '../delete/cuonsach-delete-dialog.component';

@Component({
  selector: 'jhi-cuonsach',
  templateUrl: './cuonsach.component.html',
})
export class CuonsachComponent implements OnInit {
  cuonsaches?: ICuonsach[];
  isLoading = false;
  currentSearch: string;

  constructor(protected cuonsachService: CuonsachService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.cuonsachService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<ICuonsach[]>) => {
            this.isLoading = false;
            this.cuonsaches = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.cuonsachService.query().subscribe(
      (res: HttpResponse<ICuonsach[]>) => {
        this.isLoading = false;
        this.cuonsaches = res.body ?? [];
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

  trackId(index: number, item: ICuonsach): number {
    return item.id!;
  }

  delete(cuonsach: ICuonsach): void {
    const modalRef = this.modalService.open(CuonsachDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cuonsach = cuonsach;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
