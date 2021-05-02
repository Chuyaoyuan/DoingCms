import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { OptionsService } from '../service/options.service';

import { OptionsComponent } from './options.component';

describe('Component Tests', () => {
  describe('Options Management Component', () => {
    let comp: OptionsComponent;
    let fixture: ComponentFixture<OptionsComponent>;
    let service: OptionsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [OptionsComponent],
      })
        .overrideTemplate(OptionsComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OptionsComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(OptionsService);

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
      expect(comp.options?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
