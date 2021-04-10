import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IDocgia } from '../docgia.model';
import { DocgiaService } from '../service/docgia.service';
import { DocgiaDeleteDialogComponent } from '../delete/docgia-delete-dialog.component';

@Component({
  selector: 'jhi-docgia',
  templateUrl: './docgia.component.html',
})
export class DocgiaComponent implements OnInit {
  docgias?: IDocgia[];
  isLoading = false;
  currentSearch: string;

  constructor(protected docgiaService: DocgiaService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.docgiaService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IDocgia[]>) => {
            this.isLoading = false;
            this.docgias = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.docgiaService.query().subscribe(
      (res: HttpResponse<IDocgia[]>) => {
        this.isLoading = false;
        this.docgias = res.body ?? [];
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

  trackId(index: number, item: IDocgia): number {
    return item.id!;
  }

  delete(docgia: IDocgia): void {
    const modalRef = this.modalService.open(DocgiaDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.docgia = docgia;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
