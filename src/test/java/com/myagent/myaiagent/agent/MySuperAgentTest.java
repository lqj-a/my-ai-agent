package com.myagent.myaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MySuperAgentTest {

    @Resource
    private MySuperAgent mySuperAgent;

    @Test
    void run() {
//        String userPrompt = """
//                请根据报错码D999999生成对应的故障报告，
//                并以 PDF 格式输出""";
//        String answer = mySuperAgent.run(userPrompt);
//        Assertions.assertNotNull(answer);
    }
}
