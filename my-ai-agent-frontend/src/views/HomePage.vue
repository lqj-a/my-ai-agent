<template>
  <div class="home">
    <!-- 背景光晕装饰 -->
    <div class="glow glow-1"></div>
    <div class="glow glow-2"></div>

    <header class="home-header">
      <div class="logo-badge">🤖</div>
      <h1>AI 智能体平台</h1>
      <div class="header-divider"></div>
      <p>选择一个应用开始体验</p>
    </header>

    <div class="card-grid">
      <router-link to="/my-app" class="app-card">
        <div class="card-icon-wrap ops-gradient">🛠️</div>
        <div class="card-body">
          <h2>AI 运维智能体</h2>
          <p>基于 RAG 的智能运维助手，支持多轮对话，精准回答运维相关问题。</p>
        </div>
        <span class="card-btn">开始对话 →</span>
      </router-link>

      <router-link to="/super-agent" class="app-card">
        <div class="card-icon-wrap super-gradient">⚡</div>
        <div class="card-body">
          <h2>AI 超级智能体</h2>
          <p>自主规划、工具调用、逐步执行的全能 AI 智能体，实时展示推理过程。</p>
        </div>
        <span class="card-btn">开始对话 →</span>
      </router-link>
    </div>

    <footer class="home-footer">Powered by Spring AI · DashScope · Vue 3</footer>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px 40px;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  position: relative;
  overflow: hidden;
}

/* ── 光晕 ── */
.glow {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  filter: blur(90px);
  opacity: 0.3;
}
.glow-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, #7c3aed, transparent 70%);
  top: -120px;
  right: -100px;
}
.glow-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #2563eb, transparent 70%);
  bottom: -80px;
  left: -80px;
}

/* ── Header ── */
.home-header {
  text-align: center;
  margin-bottom: 52px;
  position: relative;
  z-index: 1;
}

.logo-badge {
  font-size: 2.8rem;
  margin-bottom: 16px;
  display: inline-block;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 20px;
  width: 72px;
  height: 72px;
  line-height: 72px;
  text-align: center;
  backdrop-filter: blur(8px);
}

.home-header h1 {
  font-size: 2.4rem;
  font-weight: 700;
  background: linear-gradient(90deg, #a78bfa, #60a5fa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 12px;
}

.header-divider {
  width: 60px;
  height: 1px;
  background: rgba(255,255,255,0.15);
  margin: 12px auto;
}

.home-header p {
  font-size: 1rem;
  color: rgba(255,255,255,0.5);
}

/* ── Cards ── */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  width: 100%;
  max-width: 740px;
  position: relative;
  z-index: 1;
}

.app-card {
  background: rgba(255,255,255,0.06);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 20px;
  padding: 28px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  transition: transform 0.25s, border-color 0.25s, box-shadow 0.25s;
  cursor: pointer;
}

.app-card:hover {
  transform: translateY(-6px);
  border-color: rgba(167,139,250,0.5);
  box-shadow: 0 12px 48px rgba(124,58,237,0.25);
}

.card-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.ops-gradient {
  background: linear-gradient(135deg, #7c3aed, #4f46e5);
  box-shadow: 0 4px 16px rgba(124,58,237,0.4);
}

.super-gradient {
  background: linear-gradient(135deg, #f59e0b, #ef4444);
  box-shadow: 0 4px 16px rgba(245,158,11,0.4);
}

.card-body {
  flex: 1;
}

.app-card h2 {
  font-size: 1.2rem;
  font-weight: 600;
  color: rgba(255,255,255,0.92);
  margin-bottom: 8px;
}

.app-card p {
  font-size: 0.875rem;
  color: rgba(255,255,255,0.5);
  line-height: 1.65;
}

.card-btn {
  display: inline-block;
  font-size: 0.875rem;
  font-weight: 600;
  color: rgba(255,255,255,0.85);
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 8px;
  padding: 7px 16px;
  transition: background 0.2s;
  align-self: flex-start;
}

.app-card:hover .card-btn {
  background: rgba(167,139,250,0.25);
  border-color: rgba(167,139,250,0.4);
}

/* ── Footer ── */
.home-footer {
  margin-top: 48px;
  font-size: 0.78rem;
  color: rgba(255,255,255,0.2);
  position: relative;
  z-index: 1;
}

/* ── Responsive ── */
@media (max-width: 600px) {
  .home { padding: 40px 16px 32px; }
  .home-header h1 { font-size: 1.8rem; }
  .logo-badge { font-size: 2rem; width: 60px; height: 60px; line-height: 60px; }
}
</style>
