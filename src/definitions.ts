export interface RagPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;

  //diese ist meine neue Methode
  addTwoNumbers(options: {value1: number, value2: number}): Promise<{value: number}>;

  runInference(options: RunInferenceOptions): Promise<{text: string}>;

  runRagInference(options: {prompt: string, model: string}): Promise<{text: string}>;

}


export interface RunInferenceOptions {
  model: string;
  prompt: string;
  n_predict?: number;
  temperature?: number;
  n_ctx?: number;
  n_threads?: number;
}

