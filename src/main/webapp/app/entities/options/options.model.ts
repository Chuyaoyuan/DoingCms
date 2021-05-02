export interface IOptions {
  id?: number;
  optionName?: string;
  optionValue?: string;
  autoload?: string;
}

export class Options implements IOptions {
  constructor(public id?: number, public optionName?: string, public optionValue?: string, public autoload?: string) {}
}

export function getOptionsIdentifier(options: IOptions): number | undefined {
  return options.id;
}
