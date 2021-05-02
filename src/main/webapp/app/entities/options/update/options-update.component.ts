import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IOptions, Options } from '../options.model';
import { OptionsService } from '../service/options.service';

@Component({
  selector: 'jhi-options-update',
  templateUrl: './options-update.component.html',
})
export class OptionsUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    optionName: [null, [Validators.required]],
    optionValue: [null, [Validators.required]],
    autoload: [null, [Validators.required]],
  });

  constructor(protected optionsService: OptionsService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ options }) => {
      this.updateForm(options);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const options = this.createFromForm();
    if (options.id !== undefined) {
      this.subscribeToSaveResponse(this.optionsService.update(options));
    } else {
      this.subscribeToSaveResponse(this.optionsService.create(options));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOptions>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(options: IOptions): void {
    this.editForm.patchValue({
      id: options.id,
      optionName: options.optionName,
      optionValue: options.optionValue,
      autoload: options.autoload,
    });
  }

  protected createFromForm(): IOptions {
    return {
      ...new Options(),
      id: this.editForm.get(['id'])!.value,
      optionName: this.editForm.get(['optionName'])!.value,
      optionValue: this.editForm.get(['optionValue'])!.value,
      autoload: this.editForm.get(['autoload'])!.value,
    };
  }
}
