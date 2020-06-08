import {Injectable} from '@angular/core';
import moment, {MomentInput} from 'moment'

@Injectable({providedIn: 'root'})
export class DateService {

  constructor() {
    DateService.setLocale(window.navigator.language)
  }

  public parseIso(isoString: MomentInput) {
    return moment(isoString)
  }

  private static setLocale(locale: string) {
    moment.locale(locale)
  }
}
