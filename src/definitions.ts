export interface RagPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;

  //diese ist meine neue Methode
  addTwoNumbers(options: {value1: number, value2: number}): Promise<{value: number}>;

  runInference(options: RunInferenceOptions): Promise<{text: string}>;

  runRagInference(options: {prompt: string, model: string}): Promise<{text: string}>;

  addDocument(options: {text: string, model: string}): Promise<{ success: boolean, id: number }>;
  searchDocuments(options: {query: string}): Promise<{results: DocumentChunkResult[]}>;

}


export interface RunInferenceOptions {
  model: string;
  prompt: string;
  n_predict?: number;
  temperature?: number;
  n_ctx?: number;
  n_threads?: number;
}

// (Optional) Definiere einen Typ für die Suchergebnisse
export interface DocumentChunkResult {
  id: number;
  content: string;
}