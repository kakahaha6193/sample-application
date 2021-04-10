import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGiasach } from '../giasach.model';
import { GiasachService } from '../service/giasach.service';
import { GiasachDeleteDialogComponent } from '../delete/giasach-delete-dialog.component';

@Component({
  selector: 'jhi-giasach',
  templateUrl: './giasach.component.html',
})
export class GiasachComponent implements OnInit {
  giasaches?: IGiasach[];
  isLoading = false;
  currentSearch: string;

  constructor(protected giasachService: GiasachService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.giasachService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IGiasach[]>) => {
            this.isLoading = false;
            this.giasaches = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.giasachService.query().subscribe(
      (res: HttpResponse<IGiasach[]>) => {
        this.isLoading = false;
        this.giasaches = res.body ?? [];
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

  trackId(index: number, item: IGiasach): number {
    return item.id!;
  }

  delete(giasach: IGiasach): void {
    const modalRef = this.modalService.open(GiasachDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.giasach = giasach;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
