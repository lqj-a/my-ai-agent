package com.myagent.myaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyAppDocumentLoaderTest {
    @Resource
    private MyAppDocumentLoader myAppDocumentLoader;

    @Test
    void loadMarkdowns(){
        myAppDocumentLoader.loadMarkdowns();
    }

}