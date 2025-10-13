# rag-native-module

Offline RAG Pipeline für Android

## Install

```bash
npm install rag-native-module
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`addTwoNumbers(...)`](#addtwonumbers)
* [`runInference(...)`](#runinference)
* [`runRagInference(...)`](#runraginference)
* [`addDocument(...)`](#adddocument)
* [`searchDocuments(...)`](#searchdocuments)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### addTwoNumbers(...)

```typescript
addTwoNumbers(options: { value1: number; value2: number; }) => Promise<{ value: number; }>
```

| Param         | Type                                             |
| ------------- | ------------------------------------------------ |
| **`options`** | <code>{ value1: number; value2: number; }</code> |

**Returns:** <code>Promise&lt;{ value: number; }&gt;</code>

--------------------


### runInference(...)

```typescript
runInference(options: RunInferenceOptions) => Promise<{ text: string; }>
```

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#runinferenceoptions">RunInferenceOptions</a></code> |

**Returns:** <code>Promise&lt;{ text: string; }&gt;</code>

--------------------


### runRagInference(...)

```typescript
runRagInference(options: { prompt: string; model: string; }) => Promise<{ text: string; }>
```

| Param         | Type                                            |
| ------------- | ----------------------------------------------- |
| **`options`** | <code>{ prompt: string; model: string; }</code> |

**Returns:** <code>Promise&lt;{ text: string; }&gt;</code>

--------------------


### addDocument(...)

```typescript
addDocument(options: { text: string; }) => Promise<{ success: boolean; id: number; }>
```

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ text: string; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; id: number; }&gt;</code>

--------------------


### searchDocuments(...)

```typescript
searchDocuments(options: { query: string; }) => Promise<{ results: DocumentChunkResult[]; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ query: string; }</code> |

**Returns:** <code>Promise&lt;{ results: DocumentChunkResult[]; }&gt;</code>

--------------------


### Interfaces


#### RunInferenceOptions

| Prop              | Type                |
| ----------------- | ------------------- |
| **`model`**       | <code>string</code> |
| **`prompt`**      | <code>string</code> |
| **`n_predict`**   | <code>number</code> |
| **`temperature`** | <code>number</code> |
| **`n_ctx`**       | <code>number</code> |
| **`n_threads`**   | <code>number</code> |


#### DocumentChunkResult

| Prop          | Type                |
| ------------- | ------------------- |
| **`id`**      | <code>number</code> |
| **`content`** | <code>string</code> |

</docgen-api>
