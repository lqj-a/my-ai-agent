<template>
  <div class="chat-page">
    <header class="chat-header">
      <router-link to="/" class="back-btn">← 返回</router-link>
      <div class="header-title">
        <span class="header-icon">🛠️</span>
        <span>AI 运维智能体</span>
      </div>
      <span class="chat-id-badge" :title="chatId">会话 {{ shortId }}</span>
    </header>

    <ChatWindow
      ref="chatWindowRef"
      :messages="messages"
      :loading="loading"
      avatar="🛠️"
      @send="handleSend"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ChatWindow from '../components/ChatWindow.vue'
import { chatWithMyApp } from '../api/ai.js'

const chatId = ref('')
const messages = ref([])
const loading = ref(false)
const chatWindowRef = ref(null)

const shortId = computed(() => chatId.value.slice(0, 8))

onMounted(() => {
  chatId.value = crypto.randomUUID()
  messages.value.push({
    role: 'ai',
    text: '你好！我是 AI 运维智能体，有什么运维问题可以问我。'
  })
})

function handleSend(text) {
  if (loading.value || !text.trim()) return

  messages.value.push({ role: 'user', text })
  messages.value.push({ role: 'ai', text: '' })
  loading.value = true

  chatWithMyApp(
    text,
    chatId.value,
    (chunk) => { chatWindowRef.value?.enqueueChunk(chunk) },
    () => {
      chatWindowRef.value?.flushQueue()
      loading.value = false
    },
    () => {
      chatWindowRef.value?.flushQueue()
      loading.value = false
    }
  )
}
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f7f8fc;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  border-bottom: 1px solid rgba(255,255,255,0.08);
  flex-shrink: 0;
  z-index: 10;
}

.back-btn {
  font-size: 0.82rem;
  color: rgba(255,255,255,0.8);
  font-weight: 500;
  white-space: nowrap;
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 20px;
  padding: 4px 12px;
  transition: background 0.2s;
}
.back-btn:hover { background: rgba(255,255,255,0.18); }

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1rem;
  font-weight: 600;
  color: rgba(255,255,255,0.92);
  flex: 1;
}

.header-icon {
  font-size: 1.2rem;
}

.chat-id-badge {
  font-size: 0.72rem;
  color: rgba(255,255,255,0.5);
  background: rgba(255,255,255,0.1);
  padding: 3px 10px;
  border-radius: 20px;
  white-space: nowrap;
  border: 1px solid rgba(255,255,255,0.1);
}
</style>
