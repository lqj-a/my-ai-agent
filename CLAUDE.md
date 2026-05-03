# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build
mvn clean package

# Run locally (requires PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

# Run in Docker
docker build -t my-ai-agent:latest .
docker run -p 8123:8123 my-ai-agent:latest

# Run a single test
mvn test -Dtest=ClassName#methodName
```

Server runs on port `8123` with context path `/api`. Swagger UI at `http://localhost:8123/api/swagger-ui.html`.

## Architecture

This is a Spring Boot 3.5.13 / Java 21 AI agent framework using Alibaba DashScope (Qwen) as the LLM provider via Spring AI.

### Agent Hierarchy

The agent system uses a layered abstract class chain:

- **`BaseAgent`** — state machine (IDLE → RUNNING → FINISHED/ERROR), drives the step loop (max steps configurable), supports both sync `run()` and async streaming `runStream()` via `SseEmitter`
- **`ReActAgent`** (extends BaseAgent) — implements the Reasoning + Acting pattern; subclasses implement `think()` (call LLM, decide action) and `act()` (execute tools)
- **`ToolCallAgent`** (extends ReActAgent) — manages Spring AI tool calling; uses `withProxyToolCalls(true)` to disable Spring AI's built-in tool loop and manually controls the message context and tool execution via `ToolCallingManager`
- **`MySuperAgent`** (extends ToolCallAgent) — the production agent; max 20 steps, system prompt "MySuperAgent, an all-capable AI assistant"

### Tool System

Tools are registered as a Spring bean in `ToolRegistration` and injected into agents. Seven tools are available: `FileOperationTool`, `WebSearchTool`, `WebScrapingTool`, `ResourceDownloadTool`, `TerminalOperationTool`, `PDFGenerationTool`, and `TerminateTool`. The agent detects `TerminateTool` invocation to end the execution loop.

### RAG

RAG uses PgVector (PostgreSQL + pgvector extension, 1536-dim HNSW index). Documents are loaded from `src/main/resources/document/`. `QueryRewriter` rewrites user queries before retrieval. `QuestionAnswerAdvisor` injects retrieved context into the prompt. The system prompt enforces strict RAG-only answers (no hallucination).

### MCP (Model Context Protocol)

External MCP servers are configured in `src/main/resources/mcp-servers.json`. The `my-image-search-mcp-server/` subdirectory is a sibling Spring Boot project that exposes an image search tool (Pexels API) as an MCP server. Tools from MCP servers are loaded via `ToolCallbackProvider`.

### Chat Memory

`FileBasedChatMemory` persists conversation history to `./chat-memory/{chatId}.json`. `MessageChatMemoryAdvisor` retrieves the last 10 messages per request.

### Key Entry Points

- `AiController` — REST endpoints; `/ai/my_app/chat/*` for `MyApp`, `/ai/my_superagent/chat` for `MySuperAgent`
- `MyApp` — wires together ChatClient, RAG advisors, memory, and MCP tools for the standard chat flow
- `MySuperAgent` — autonomous agent endpoint that streams step-by-step execution via SSE

## Configuration Profiles

- `local` — connects to `localhost:5432`
- `prod` — connects to `host.docker.internal:5432` (for Docker)

API keys (DashScope, search, Pexels, AMap) are stored in `application-local.yml` and `mcp-servers.json`. Use environment variable overrides for any deployment beyond local dev.

## Dependencies to Know

- **Spring AI** `1.0.0-M6/M7` — milestone release; APIs may differ from GA docs
- **spring-ai-alibaba** `1.0.0-M6.1` — DashScope/Qwen integration
- **Hutool** — utility library used throughout (file ops, HTTP, etc.)
- **iText 9** — PDF generation in `PDFGenerationTool`
- **JSoup** — HTML scraping in `WebScrapingTool`
