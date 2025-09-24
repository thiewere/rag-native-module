export interface RagPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;

  //diese ist meine neue Methode
  addTwoNumbers(options: {value1: number, value2: number}): Promise<{value: number}>;
}
