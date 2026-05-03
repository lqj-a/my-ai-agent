package com.myagent.myaiagent.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpAiInvoke {
    public static void main(String[] args) {
        // 1. 你的 API-KEY（替换成真实密钥）
        String apiKey = TestApiKey.API_KEY;

        // 2. 接口地址
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        // 3. 构建请求体 JSON（和你 curl 里的 data 完全一致）
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", "qwen-plus");

        // input -> messages
        Map<String, Object> input = new HashMap<>();
        Map<String, String> msg1 = new HashMap<>();
        msg1.put("role", "system");
        msg1.put("content", "You are a helpful assistant.");

        Map<String, String> msg2 = new HashMap<>();
        msg2.put("role", "user");
        msg2.put("content", "你是谁？");

        input.put("messages", List.of(msg1, msg2));
        bodyMap.put("input", input);

        // parameters
        Map<String, Object> params = new HashMap<>();
        params.put("result_format", "message");
        bodyMap.put("parameters", params);

        // 4. 发送 POST JSON 请求（Hutool）
        String result = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(bodyMap))  // 转 JSON
                .timeout(20000)                    // 超时 20s
                .execute()
                .body();

        // 输出结果
        System.out.println("响应结果：");
        System.out.println(result);
    }
}

/**
 * curl --location "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation" \
 * --header "Authorization: Bearer $DASHSCOPE_API_KEY" \
 * --header "Content-Type: application/json" \
 * --data '{
 *     "model": "qwen-plus",
 *     "input":{
 *         "messages":[
 *             {
 *                 "role": "system",
 *                 "content": "You are a helpful assistant."
 *             },
 *             {
 *                 "role": "user",
 *                 "content": "你是谁？"
 *             }
 *         ]
 *     },
 *     "parameters": {
 *         "result_format": "message"
 *     }
 * }'
 *
 *
 */