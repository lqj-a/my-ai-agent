package com.myagent.myaiagent;

import org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class MyAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyAiAgentApplication.class, args);
    }

}
