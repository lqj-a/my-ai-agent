//package com.myagent.myaiagent.rag;
//
//import com.knuddels.jtokkit.api.EncodingType;
//import org.springframework.ai.embedding.BatchingStrategy;
//import org.springframework.ai.embedding.TokenCountBatchingStrategy;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Spring AI 提供了一个名为 TokenCountBatchingStrategy 的默认实现。这个策略为每个文档估算 token 数，
// * 将文档分组到不超过最大输入 token 数的批次中，如果单个文档超过此限制，则抛出异常。这样就确保了每个批次不‌超过计算出的最大输入 token 数。
// *
// */
//@Configuration
//public class EmbeddingConfig {
//    @Bean
//    public BatchingStrategy customTokenCountBatchingStrategy() {
//        return new TokenCountBatchingStrategy(
//                EncodingType.CL100K_BASE,
//                8000,
//                0.1
//        );
//    }
//}