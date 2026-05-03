package com.myagent.myaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebSearchToolTest {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Test
    public void testSearchWeb() {
//        WebSearchTool tool = new WebSearchTool(searchApiKey);
//        String query = "百度 baidu.com";
//        String result = tool.searchWeb(query);
//        System.out.println("result" + result);
//        assertNotNull(result);
    }
}