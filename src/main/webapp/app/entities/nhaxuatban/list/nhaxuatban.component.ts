import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INhaxuatban } from '../nhaxuatban.model';
import { NhaxuatbanService } from '../service/nhaxuatban.service';
import { NhaxuatbanDeleteDialogComponent } from '../delete/nhaxuatban-delete-dialog.component';

@Component({
  selector: 'jhi-nhaxuatban',
  templateUrl: './nhaxuatban.component.html',
})
export class NhaxuatbanComponent implements OnInit {
  nhaxuatbans?: INhaxuatban[];
  isLoading = false;
  currentSearch: string;

  constructor(protected nhaxuatbanService: NhaxuatbanService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.nhaxuatbanService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<INhaxuatban[]>) => {
            this.isLoading = false;
            this.nhaxuatbans = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.nhaxuatbanService.query().subscribe(
      (res: HttpResponse<INhaxuatban[]>) => {
        this.isLoading = false;
        this.nhaxuatbans = res.body ?? [];
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

  trackId(index: number, item: INhaxuatban): number {
    return item.id!;
  }

  delete(nhaxuatban: INhaxuatban): void {
    const modalRef = this.modalService.open(NhaxuatbanDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.nhaxuatban = nhaxuatban;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
