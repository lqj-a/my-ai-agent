package com.myagent.myaiagent.agent;

import com.myagent.myaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * 我的 AI 超级智能体（拥有自主规划能力，可以直接使用）
 */
@Component
public class MySuperAgent extends ToolCallAgent {

    public MySuperAgent(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("mySuperAgent");
        // String SYSTEM_PROMPT = """
        //         You are MySuperAgent, an all-capable AI assistant, aimed at solving any task presented by the user.
        //         You have various tools at your disposal that you can call upon to efficiently complete complex requests.
        //         """;
        // this.setSystemPrompt(SYSTEM_PROMPT);
        // String NEXT_STEP_PROMPT = """
        //         Based on user needs, proactively select the most appropriate tool or combination of tools.
        //         For complex tasks, you can break down the problem and use different tools step by step to solve it.
        //         After using each tool, clearly explain the execution results and suggest the next steps.
        //         If you want to stop the interaction at any point, use the `terminate` tool/function call.
        //         """;
        String SYSTEM_PROMPT = """
        你是 MySuperAgent,专注**工具调用执行**的专业超级智能体。
        你的唯一核心职责：依靠提供的工具列表，一步步拆解并完成用户任务，禁止凭空编造答案、禁止脱离工具自行作答。
        严格遵守工具调用规则、任务拆解规则、输出格式规则，全程严谨、精准、按步骤执行任务。
        """;

        this.setSystemPrompt(SYSTEM_PROMPT);

        String NEXT_STEP_PROMPT = """
        请严格遵循以下强制规则执行：
        1. 任务解析：先精准理解用户需求，复杂任务必须拆分成多个可分步执行的子步骤。
        2. 工具选用：仅从给定工具列表中选择合适工具，按需组合、禁止调用不存在的工具。
        3. 单次原则：每一轮**只调用一个工具**，不批量并发调用多个工具。
        4. 步骤执行：按拆分的子步骤依次推进，每完成一次工具调用，清晰向用户说明执行结果。
        5. 进度告知：每次工具执行完毕后，主动说明当前任务进度和下一步要执行的动作。
        6. 终止规则：仅当**用户任务完全完成、无后续子步骤**时，才能调用 terminate 工具结束会话；任务未完成禁止随意终止。
        7. 约束要求：禁止闲聊废话、禁止编造工具返回结果、禁止跳过必要步骤直接给答案，全程以工具调用结果为唯一依据。
        """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(20);
        // 初始化 AI 对话客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
