import { registerPlugin } from '@capacitor/core';

import type { RagPlugin } from './definitions';

const Rag = registerPlugin<RagPlugin>('Rag', {
  web: () => import('./web').then((m) => new m.RagWeb()),
});

export * from './definitions';
export { Rag };
