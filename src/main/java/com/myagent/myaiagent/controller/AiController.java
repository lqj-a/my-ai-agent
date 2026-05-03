package com.myagent.myaiagent.controller;

import com.myagent.myaiagent.agent.MySuperAgent;
import com.myagent.myaiagent.app.MyApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private MyApp myApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用 AI 运维智能体应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/my_app/chat/sync")
    public String doChatWithMyAppSync(String message, String chatId) {
        return myApp.doChat(message, chatId);
    }

    /**
     * SSE 流式调用 AI 运维智能体应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/my_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithMyAppSSE(String message, String chatId) {
        return myApp.doChatByStream(message, chatId);
    }

    /**
     * SSE 流式调用 AI 运维智能体应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/my_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithMyAppServerSentEvent(String message, String chatId) {
        return myApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 流式调用 AI 运维智能体应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/my_app/chat/sse_emitter")
    public SseEmitter doChatWithMyAppServerSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L); // 3 分钟超时
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
        myApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        // 返回
        return sseEmitter;
    }

    /**
     * 流式调用超级智能体
     *
     * @param message
     * @return
     */
    @GetMapping("/my_superagent/chat")
    public SseEmitter doChatWithMySuperAgent(String message) {
        MySuperAgent mySuperAgent = new MySuperAgent(allTools, dashscopeChatModel);
        return mySuperAgent.runStream(message);
    }
}
