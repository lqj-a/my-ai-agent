<template>
  <div class="chat-window">
    <!-- Messages area -->
    <div class="messages" ref="messagesEl">
      <div
        v-for="(msg, i) in messages"
        :key="i"
        class="message-row"
        :class="msg.role"
      >
        <!-- AI avatar -->
        <div v-if="msg.role === 'ai'" class="avatar ai-avatar">{{ avatar }}</div>

        <div class="bubble" :class="msg.role">
          <!-- AI bubbles: render markdown; user bubbles: plain text -->
          <div v-if="msg.role === 'ai'" class="md-body" v-html="renderMarkdown(msg.text)"></div>
          <span v-else class="bubble-text">{{ msg.text }}</span>
          <!-- Blinking cursor while streaming the last AI message -->
          <span
            v-if="loading && msg.role === 'ai' && i === messages.length - 1"
            class="cursor"
          ></span>
        </div>

        <!-- User avatar -->
        <div v-if="msg.role === 'user'" class="avatar user-avatar">👤</div>
      </div>

      <!-- Thinking indicator when AI hasn't started replying yet -->
      <div v-if="loading && messages[messages.length - 1]?.role === 'ai' && messages[messages.length - 1]?.text === ''" class="thinking">
        <span></span><span></span><span></span>
      </div>
    </div>

    <!-- Input bar -->
    <div class="input-bar">
      <textarea
        v-model="inputText"
        class="input-field"
        placeholder="输入消息，按 Enter 发送，Shift+Enter 换行"
        rows="1"
        :disabled="loading"
        @keydown.enter.exact.prevent="submit"
        @input="autoResize"
        ref="textareaEl"
      ></textarea>
      <button class="send-btn" :disabled="loading || !inputText.trim()" @click="submit">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="22" y1="2" x2="11" y2="13"></line>
          <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onUnmounted } from 'vue'
import { marked } from 'marked'
import { markedHighlight } from 'marked-highlight'
import hljs from 'highlight.js'

marked.use(markedHighlight({
  langPrefix: 'hljs language-',
  highlight(code, lang) {
    const language = hljs.getLanguage(lang) ? lang : 'plaintext'
    return hljs.highlight(code, { language }).value
  }
}))
marked.use({ breaks: true, gfm: true })

const props = defineProps({
  messages: { type: Array, required: true },
  loading: { type: Boolean, default: false },
  avatar: { type: String, default: '🤖' }
})

const emit = defineEmits(['send'])

const inputText = ref('')
const messagesEl = ref(null)
const textareaEl = ref(null)

// ── Typewriter engine ──────────────────────────────────────────────
const pendingQueue = ref([])
let typingTimer = null
const TYPING_INTERVAL_MS = 16

function startTyping() {
  if (typingTimer !== null) return
  typingTimer = setInterval(() => {
    if (pendingQueue.value.length === 0) {
      clearInterval(typingTimer)
      typingTimer = null
      return
    }
    const charsPerTick = Math.max(1, Math.floor(pendingQueue.value.length / 40))
    const batch = pendingQueue.value.splice(0, charsPerTick)
    const lastMsg = props.messages[props.messages.length - 1]
    if (lastMsg && lastMsg.role === 'ai') {
      lastMsg.text += batch.join('')
    }
  }, TYPING_INTERVAL_MS)
}

function enqueueChunk(chunk) {
  pendingQueue.value.push(...chunk.split(''))
  startTyping()
}

function flushQueue() {
  if (pendingQueue.value.length === 0) return
  const lastMsg = props.messages[props.messages.length - 1]
  if (lastMsg && lastMsg.role === 'ai') {
    lastMsg.text += pendingQueue.value.join('')
  }
  pendingQueue.value = []
  if (typingTimer !== null) {
    clearInterval(typingTimer)
    typingTimer = null
  }
}

onUnmounted(() => {
  if (typingTimer !== null) clearInterval(typingTimer)
})

defineExpose({ enqueueChunk, flushQueue })
// ──────────────────────────────────────────────────────────────────

function renderMarkdown(text) {
  if (!text) return ''
  let s = text
    .replace(/\\n/g, '\n')
    .replace(/\\t/g, '\t')
    .replace(/\\r/g, '')

  // Ensure headings, list items, blockquotes, and code fences
  // always have a blank line before them so marked parses them correctly
  s = s.replace(/([^\n])\n(#{1,6} )/g, '$1\n\n$2')   // headings
  s = s.replace(/([^\n])\n([-*+] |\d+\. )/g, '$1\n\n$2') // list items
  s = s.replace(/([^\n])\n(```)/g, '$1\n\n$2')         // code fences
  s = s.replace(/([^\n])\n(> )/g, '$1\n\n$2')          // blockquotes

  return marked.parse(s)
}

function submit() {
  const text = inputText.value.trim()
  if (!text || props.loading) return
  emit('send', text)
  inputText.value = ''
  nextTick(() => {
    if (textareaEl.value) textareaEl.value.style.height = 'auto'
  })
}

function autoResize(e) {
  const el = e.target
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 160) + 'px'
}

watch(
  () => props.messages.map(m => m.text).join(''),
  () => {
    nextTick(() => {
      if (messagesEl.value) {
        messagesEl.value.scrollTop = messagesEl.value.scrollHeight
      }
    })
  }
)
</script>

<style scoped>
.chat-window {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

/* ── Messages ── */
.messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: #f7f8fc;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  max-width: 100%;
}

.message-row.user {
  flex-direction: row-reverse;
}

/* ── Avatars ── */
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  flex-shrink: 0;
  margin-top: 2px;
}

.ai-avatar {
  background: linear-gradient(135deg, #a78bfa, #60a5fa);
  box-shadow: 0 2px 8px rgba(167,139,250,0.4);
}

.user-avatar {
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 2px 8px rgba(102,126,234,0.4);
}

/* ── Bubbles ── */
.bubble {
  max-width: min(72%, 640px);
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 0.95rem;
  line-height: 1.65;
  word-break: break-word;
  position: relative;
}

.bubble.ai {
  background: #fff;
  color: #1a1a1a;
  border-top-left-radius: 4px;
  border-left: 3px solid #a78bfa;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  text-align: left;
}

.bubble.user {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border-top-right-radius: 4px;
  box-shadow: 0 2px 12px rgba(102,126,234,0.35);
}

.bubble-text {
  white-space: pre-wrap;
}

/* ── Markdown body ── */
.md-body {
  text-align: left;
  line-height: 1.7;
}

.md-body :deep(p) { margin: 0 0 0.6em; }
.md-body :deep(p:last-child) { margin-bottom: 0; }

.md-body :deep(h1),
.md-body :deep(h2),
.md-body :deep(h3),
.md-body :deep(h4) {
  margin: 0.9em 0 0.4em;
  font-weight: 600;
  line-height: 1.3;
}
.md-body :deep(h1) { font-size: 1.25em; }
.md-body :deep(h2) { font-size: 1.1em; }
.md-body :deep(h3) { font-size: 1em; }

.md-body :deep(ul),
.md-body :deep(ol) {
  margin: 0.4em 0 0.6em;
  padding-left: 1.4em;
}
.md-body :deep(li) { margin: 0.2em 0; }

.md-body :deep(blockquote) {
  margin: 0.6em 0;
  padding: 0.4em 0.8em;
  border-left: 3px solid #a78bfa;
  background: #f5f3ff;
  border-radius: 0 6px 6px 0;
  color: #555;
}

.md-body :deep(hr) {
  border: none;
  border-top: 1px solid #eee;
  margin: 0.8em 0;
}

.md-body :deep(a) {
  color: #7c3aed;
  text-decoration: underline;
  word-break: break-all;
}

.md-body :deep(code) {
  font-family: 'JetBrains Mono', 'Fira Code', Consolas, monospace;
  font-size: 0.875em;
  background: #f0eeff;
  color: #7c3aed;
  padding: 0.15em 0.4em;
  border-radius: 4px;
}

.md-body :deep(pre) {
  margin: 0.6em 0;
  border-radius: 10px;
  overflow: hidden;
  background: #1e1e2e;
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.md-body :deep(pre code) {
  display: block;
  padding: 14px 16px;
  overflow-x: auto;
  font-size: 0.85em;
  line-height: 1.6;
  background: transparent;
  color: #cdd6f4;
  border-radius: 0;
}

.md-body :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 0.6em 0;
  font-size: 0.9em;
}
.md-body :deep(th),
.md-body :deep(td) {
  border: 1px solid #e8e0ff;
  padding: 6px 12px;
  text-align: left;
}
.md-body :deep(th) {
  background: #f5f3ff;
  font-weight: 600;
  color: #5b21b6;
}
.md-body :deep(tr:nth-child(even) td) { background: #faf9ff; }

/* ── Blinking cursor ── */
.cursor {
  display: inline-block;
  width: 2px;
  height: 1em;
  background: #a78bfa;
  margin-left: 2px;
  vertical-align: text-bottom;
  animation: blink 0.8s step-end infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* ── Thinking dots ── */
.thinking {
  display: flex;
  gap: 5px;
  padding: 8px 16px;
  margin-left: 46px;
}

.thinking span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #c4b5fd;
  animation: bounce 1.2s infinite ease-in-out;
}

.thinking span:nth-child(2) { animation-delay: 0.2s; }
.thinking span:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.7); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* ── Input bar ── */
.input-bar {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  padding: 14px 18px;
  background: #fff;
  border-top: 1px solid #ede9fe;
  box-shadow: 0 -2px 12px rgba(0,0,0,0.04);
  flex-shrink: 0;
}

.input-field {
  flex: 1;
  resize: none;
  border: 1.5px solid #e0d9ff;
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 0.95rem;
  font-family: inherit;
  line-height: 1.5;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  max-height: 160px;
  overflow-y: auto;
  background: #faf9ff;
}

.input-field:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 3px rgba(167,139,250,0.15);
  background: #fff;
}

.input-field:disabled { background: #f5f3ff; color: #aaa; }

.send-btn {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: opacity 0.2s, transform 0.15s;
  box-shadow: 0 2px 10px rgba(102,126,234,0.4);
}

.send-btn svg { width: 18px; height: 18px; }
.send-btn:hover:not(:disabled) { opacity: 0.88; transform: scale(1.05); }
.send-btn:disabled { opacity: 0.35; cursor: not-allowed; box-shadow: none; }

/* ── Responsive ── */
@media (max-width: 600px) {
  .messages { padding: 14px 10px; gap: 14px; }
  .bubble { max-width: 86%; font-size: 0.9rem; padding: 10px 13px; }
  .avatar { width: 30px; height: 30px; font-size: 0.9rem; }
  .input-bar { padding: 10px 12px; }
  .md-body :deep(pre code) { font-size: 0.8em; }
}
</style>
