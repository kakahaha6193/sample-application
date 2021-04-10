jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ThuthuService } from '../service/thuthu.service';

import { ThuthuComponent } from './thuthu.component';

describe('Component Tests', () => {
  describe('Thuthu Management Component', () => {
    let comp: ThuthuComponent;
    let fixture: ComponentFixture<ThuthuComponent>;
    let service: ThuthuService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ThuthuComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(ThuthuComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ThuthuComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ThuthuService);

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
      expect(comp.thuthus?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
