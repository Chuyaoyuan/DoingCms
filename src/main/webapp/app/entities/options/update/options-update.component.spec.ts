jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { OptionsService } from '../service/options.service';
import { IOptions, Options } from '../options.model';

import { OptionsUpdateComponent } from './options-update.component';

describe('Component Tests', () => {
  describe('Options Management Update Component', () => {
    let comp: OptionsUpdateComponent;
    let fixture: ComponentFixture<OptionsUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let optionsService: OptionsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [OptionsUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(OptionsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OptionsUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      optionsService = TestBed.inject(OptionsService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const options: IOptions = { id: 456 };

        activatedRoute.data = of({ options });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(options));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const options = { id: 123 };
        spyOn(optionsService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ options });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: options }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(optionsService.update).toHaveBeenCalledWith(options);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const options = new Options();
        spyOn(optionsService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ options });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: options }));
        saveSubject.complete();

        // THEN
        expect(optionsService.create).toHaveBeenCalledWith(options);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const options = { id: 123 };
        spyOn(optionsService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ options });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(optionsService.update).toHaveBeenCalledWith(options);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
