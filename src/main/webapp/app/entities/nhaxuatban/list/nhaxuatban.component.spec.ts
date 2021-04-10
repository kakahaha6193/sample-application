jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NhaxuatbanService } from '../service/nhaxuatban.service';

import { NhaxuatbanComponent } from './nhaxuatban.component';

describe('Component Tests', () => {
  describe('Nhaxuatban Management Component', () => {
    let comp: NhaxuatbanComponent;
    let fixture: ComponentFixture<NhaxuatbanComponent>;
    let service: NhaxuatbanService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [NhaxuatbanComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(NhaxuatbanComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(NhaxuatbanComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(NhaxuatbanService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.nhaxuatbans?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
