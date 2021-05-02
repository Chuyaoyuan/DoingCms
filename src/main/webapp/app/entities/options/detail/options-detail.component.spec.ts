import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OptionsDetailComponent } from './options-detail.component';

describe('Component Tests', () => {
  describe('Options Management Detail Component', () => {
    let comp: OptionsDetailComponent;
    let fixture: ComponentFixture<OptionsDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [OptionsDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ options: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(OptionsDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(OptionsDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load options on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.options).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
