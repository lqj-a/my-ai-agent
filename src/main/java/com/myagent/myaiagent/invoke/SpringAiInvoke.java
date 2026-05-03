package com.myagent.myaiagent.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * spring ai 调用大模型
 */
@Component
public class SpringAiInvoke implements CommandLineRunner {
    //自动注入dashscopeChatModel的bean   DashScopeAutoConfiguration.class 206line
    @Resource
    private ChatModel dashscopeChatModel;


    @Override
    public void run(String... args) throws Exception {
        AssistantMessage assistantMessage = dashscopeChatModel.call(new Prompt("哈哈"))
                .getResult()
                .getOutput();
        System.out.println(assistantMessage.getText());

    }
}
