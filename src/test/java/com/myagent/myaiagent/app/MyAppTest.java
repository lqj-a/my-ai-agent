package com.myagent.myaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class MyAppTest {

    @Resource
    private MyApp myApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好";
        String answer = myApp.doChat(message, chatId);

        // 第二轮
        message = "数据库报错码 D999999 怎么处理";
        answer = myApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第二轮
        message = "刚刚那个错误具体是什么原因？";
        answer = myApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);


    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，数据库连接超时故障有什么具体原因？";
        MyApp.MyReport myReport = myApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(myReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "GoldenDB 安装目录磁盘使用率超限是什么原因，怎么处理" ;
        String answer =  myApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {


//
        testMessage("删除/tmp/file 下的txt文件");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = myApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();

//        String message = "帮我搜索一些数据库报错相关的图片";
//        String answer =  myApp.doChatWithMcp(message, chatId);
//        Assertions.assertNotNull(answer);
    }


}