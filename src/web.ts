import { WebPlugin } from '@capacitor/core';

import type { RagPlugin } from './definitions';

export class RagWeb extends WebPlugin implements RagPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
