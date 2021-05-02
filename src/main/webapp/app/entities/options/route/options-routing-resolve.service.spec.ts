jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IOptions, Options } from '../options.model';
import { OptionsService } from '../service/options.service';

import { OptionsRoutingResolveService } from './options-routing-resolve.service';

describe('Service Tests', () => {
  describe('Options routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: OptionsRoutingResolveService;
    let service: OptionsService;
    let resultOptions: IOptions | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(OptionsRoutingResolveService);
      service = TestBed.inject(OptionsService);
      resultOptions = undefined;
    });

    describe('resolve', () => {
      it('should return IOptions returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOptions = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultOptions).toEqual({ id: 123 });
      });

      it('should return new IOptions if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOptions = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultOptions).toEqual(new Options());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOptions = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultOptions).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
