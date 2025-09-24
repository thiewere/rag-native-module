import { WebPlugin } from '@capacitor/core';

import type { RagPlugin } from './definitions';

export class RagWeb extends WebPlugin implements RagPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  // diese ist meine Implementierung in Web
  async addTwoNumbers(options: { value1: number; value2: number; }): Promise<{ value: number}> {
    const summe = options.value1 + options.value2;
    console.log("SUMME: ", summe)
    return { value: summe};
  }
}
