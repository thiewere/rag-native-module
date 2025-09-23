export interface RagPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
