const BASE_URL = '/api'

/**
 * Open an SSE connection to the MyApp chat endpoint.
 * @param {string} message
 * @param {string} chatId
 * @param {(chunk: string) => void} onChunk  called for each data chunk
 * @param {() => void} onDone               called when stream ends
 * @param {(err: Event) => void} onError
 * @returns {EventSource}
 */
export function chatWithMyApp(message, chatId, onChunk, onDone, onError) {
  const url = `${BASE_URL}/ai/my_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  const es = new EventSource(url)

  es.onmessage = (e) => {
    onChunk(e.data)
  }

  es.addEventListener('done', () => {
    es.close()
    onDone()
  })

  es.onerror = (e) => {
    es.close()
    // A normal stream end also triggers onerror in some browsers after the server closes
    onDone()
    if (onError) onError(e)
  }

  return es
}

/**
 * Open an SSE connection to the SuperAgent endpoint.
 * Each message event represents one completed agent step.
 */
export function chatWithSuperAgent(message, onChunk, onDone, onError) {
  const url = `${BASE_URL}/ai/my_superagent/chat?message=${encodeURIComponent(message)}`
  const es = new EventSource(url)

  es.onmessage = (e) => {
    onChunk(e.data)
  }

  es.addEventListener('done', () => {
    es.close()
    onDone()
  })

  es.onerror = (e) => {
    es.close()
    onDone()
    if (onError) onError(e)
  }

  return es
}
