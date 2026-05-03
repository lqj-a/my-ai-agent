# My AI Agent

基于 Spring Boot + Spring AI 构建的 AI 智能体框架，集成 Alibaba DashScope（通义千问）作为 LLM 提供商，支持 RAG 知识库问答、自主工具调用、MCP 协议扩展和流式输出。

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.5.13 / Java 21 |
| AI 框架 | Spring AI 1.0.0-M6/M7 |
| LLM 提供商 | Alibaba DashScope（通义千问）via spring-ai-alibaba 1.0.0-M6.1 |
| 向量数据库 | PostgreSQL + pgvector（1536 维，HNSW 索引） |
| 前端 | Vue 3.4 + Vite 5 |
| 容器化 | Docker（Node 20-Alpine + OpenJDK 21 JRE） |

## 项目结构

```
my-ai-agent/
├── src/main/java/com/myagent/myaiagent/
│   ├── agent/          # 智能体层（BaseAgent → ReActAgent → ToolCallAgent → MySuperAgent）
│   ├── tools/          # 工具实现（7 个工具）
│   ├── app/            # MyApp 标准对话应用
│   ├── rag/            # RAG 知识库组件
│   ├── advisor/        # Spring AI Advisor（日志、Re-Reading）
│   ├── chatmemory/     # 基于文件的对话记忆
│   ├── controller/     # REST API 入口
│   ├── config/         # 配置（CORS 等）
│   └── invoke/         # 多种 AI 调用方式示例
├── src/main/resources/
│   ├── document/       # RAG 知识库文档
│   └── mcp-servers.json
├── my-ai-agent-frontend/        # Vue 3 前端
└── my-image-search-mcp-server/  # 图片搜索 MCP 服务（子项目）
```

## 核心架构

### 详细架构
https://github.com/lqj-a/my-ai-agent/blob/main/architecture.md

### 智能体层级

```
BaseAgent
  状态机（IDLE → RUNNING → FINISHED/ERROR），驱动步骤循环，支持 SSE 流式输出
  └── ReActAgent
        实现 Reasoning + Acting 模式，定义 think() / act() 抽象方法
        └── ToolCallAgent
              管理 Spring AI 工具调用，手动维护消息上下文
              └── MySuperAgent
                    自主规划智能体，最多 20 步
```

### 工具列表（7 个）

| 工具 | 功能 |
|------|------|
| `WebSearchTool` | 网络搜索 |
| `WebScrapingTool` | 网页内容抓取（JSoup） |
| `FileOperationTool` | 文件读写操作 |
| `TerminalOperationTool` | 终端命令执行 |
| `ResourceDownloadTool` | 资源下载 |
| `PDFGenerationTool` | PDF 生成（iText 9） |
| `TerminateTool` | 终止智能体执行循环 |

### RAG 知识库

- 文档存放于 `src/main/resources/document/`，启动时自动加载入向量库
- `QueryRewriter` 对用户问题进行改写后再检索
- `QuestionAnswerAdvisor` 将检索结果注入 Prompt，严格限制模型只基于知识库回答

### 对话记忆

`FileBasedChatMemory` 将对话历史持久化到 `./chat-memory/{chatId}.json`。

### MCP 扩展

外部 MCP 服务器配置在 `src/main/resources/mcp-servers.json`。内置子项目 `my-image-search-mcp-server` 通过 Pexels API 提供图片搜索能力。

## 快速开始

### 前置条件

- Java 21+
- Maven 3.9+
- PostgreSQL（需安装 pgvector 扩展）
- Node.js 20+（前端开发）

### 配置

在 `src/main/resources/application-local.yml` 中填写：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_db
    username: your_user
    password: your_password
  ai:
    dashscope:
      api-key: your_dashscope_api_key
```

在 `src/main/resources/mcp-servers.json` 中填写 AMap / Pexels API Key。

### 本地运行

```bash
# 构建
./mvnw clean package

# 启动后端（本地 profile）
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

# 启动前端开发服务器
cd my-ai-agent-frontend
npm install
npm run dev
```

后端服务启动后：
- API 地址：`http://localhost:8123/api`
- Swagger UI：`http://localhost:8123/api/swagger-ui.html`

### Docker 运行

```bash
# 先构建 JAR
./mvnw clean package -DskipTests

# 构建镜像
docker build -t my-ai-agent:latest .

# 运行（需要外部 PostgreSQL 可访问）
docker run -p 8123:8123 my-ai-agent:latest
```

Docker 模式使用 `prod` profile，数据库地址为 `host.docker.internal:5432`。

## API 接口

### MyApp（RAG 对话）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/ai/my_app/chat/sync` | 同步对话 |
| GET | `/ai/my_app/chat/sse` | SSE 流式对话 |
| GET | `/ai/my_app/chat/sse_emitter` | SseEmitter 流式（3 分钟超时） |

参数：`message`（必填）、`chatId`（可选，用于关联对话记忆）

### MySuperAgent（自主智能体）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/ai/my_superagent/chat` | 自主规划并执行任务，SSE 逐步输出 |

参数：`message`（必填）

每个 SSE 事件格式：

```
Step N: 思考：<AI 推理过程>
工具 <toolName> 返回的结果：<result>
```

## 前端

Vue 3 + Vite 单页应用，包含三个页面：

- **首页**：功能入口导航
- **MyApp 对话**：RAG 知识库问答，支持 Markdown 渲染和代码高亮
- **超级智能体**：自主任务执行，实时展示每步思考过程和工具调用结果

```bash
cd my-ai-agent-frontend
npm run build    # 生产构建，输出到 dist/
npm run preview  # 预览生产构建
```

## 测试

```bash
# 运行全部测试
./mvnw test

# 运行单个测试
./mvnw test -Dtest=ClassName#methodName
```

## 配置 Profile

| Profile | 数据库地址 | 适用场景 |
|---------|-----------|---------|
| `local` | `localhost:5432` | 本地开发 |
| `prod` | `host.docker.internal:5432` | Docker 部署 |

敏感配置（API Key 等）存放在 `application-local.yml` 和 `mcp-servers.json` 中，不提交到版本库，生产环境建议通过环境变量覆盖。
