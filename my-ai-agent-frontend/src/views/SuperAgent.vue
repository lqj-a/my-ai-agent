<template>
  <div class="chat-page">
    <header class="chat-header">
      <router-link to="/" class="back-btn">← 返回</router-link>
      <div class="header-title">
        <span class="header-icon">⚡</span>
        <span>AI 超级智能体</span>
      </div>
    </header>

    <ChatWindow
      ref="chatWindowRef"
      :messages="messages"
      :loading="loading"
      avatar="⚡"
      @send="handleSend"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ChatWindow from '../components/ChatWindow.vue'
import { chatWithSuperAgent } from '../api/ai.js'

const messages = ref([])
const loading = ref(false)
const chatWindowRef = ref(null)

onMounted(() => {
  messages.value.push({
    role: 'ai',
    text: '你好！我是 AI 超级智能体，可以自主规划并执行复杂任务，请告诉我你的需求。'
  })
})

function handleSend(text) {
  if (loading.value || !text.trim()) return

  messages.value.push({ role: 'user', text })
  messages.value.push({ role: 'ai', text: '' })
  loading.value = true

  chatWithSuperAgent(
    text,
    // Each SSE event = one completed step; append with extra newline then typewrite
    (chunk) => { chatWindowRef.value?.enqueueChunk(chunk + '\n\n') },
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
</style>
