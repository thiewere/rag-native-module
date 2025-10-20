import { WebPlugin } from '@capacitor/core';

import type { RagPlugin, RunInferenceOptions, DocumentChunkResult  } from './definitions';


export class RagWeb extends WebPlugin implements RagPlugin {

  private _llama: any | null = null;
  private _context: any | null = null;

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

   private async ensureLlama() {
    if (this._llama) return;
    // try {
    //   // DYNAMISCH importieren — wenn Host die peerDependency nicht installiert hat,
    //   // wirft das und können eine hilfreiche Fehlermeldung zurückgeben.
      
    //   this._llama = await import('llama-cpp-capacitor');
    // } catch (err) {
    //   throw new Error(
    //     'llama-cpp-capacitor nicht gefunden. Installiere es in der Host-App (rag-native-app) als Abhängigkeit.'
    //   );
    // }
  }

   public async runInference(options: RunInferenceOptions): Promise<{ text: string }> {
    await this.ensureLlama();

    // Lazy init des Llama-Kontexts
    if (!this._context) {
      this._context = await this._llama.initLlama({
        model: options.model,
        n_ctx: options.n_ctx ?? 1024,
        n_threads: options.n_threads ?? 4,
      });
    }

    const result = await this._context.completion({
      prompt: options.prompt,
      n_predict: options.n_predict ?? 50,
      temperature: options.temperature ?? 0.7,
    });

    // frmats: manche versionen liefern `text`, andere `content`
    const text = result?.text ?? result?.content ?? String(result);
    return { text };
  }

  async runRagInference(options: {prompt: string, model: string}): Promise<{text: string}> {
    const res = options.prompt;
    return {text: res};
  }

  async addDocument(options: { text: string; }): Promise<{  success: boolean, id: number  }> {
    console.log("Web nicht unterstützt", options.text)
  
    return {success: true, id: 1}
  }

  async searchDocuments(options: { query: string; }): Promise<{ results: DocumentChunkResult[]}> {
    console.log("Web Fall-Back: " + options.query)
    return {results: []}
  }

}
