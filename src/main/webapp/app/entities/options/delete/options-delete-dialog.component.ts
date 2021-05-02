import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOptions } from '../options.model';
import { OptionsService } from '../service/options.service';

@Component({
  templateUrl: './options-delete-dialog.component.html',
})
export class OptionsDeleteDialogComponent {
  options?: IOptions;

  constructor(protected optionsService: OptionsService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.optionsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
