package com.myagent.myaiagent.rag;

import jakarta.annotation.Resource;
import org.apache.commons.codec.binary.Hex;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
public class PgVectorVectorStoreConfig {  //实现增量发布

    @Resource
    private MyAppDocumentLoader myAppDocumentLoader;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        PgVectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1536)
                .distanceType(COSINE_DISTANCE)
                .indexType(HNSW)
                .initializeSchema(true)
                .schemaName("public")
                .vectorTableName("vector_store")
                .maxDocumentBatchSize(10000)
                .build();

        // 创建文档状态表
        createDocumentStateTable();

        // 加载所有文档
        List<Document> allDocuments = myAppDocumentLoader.loadMarkdowns();

        // 过滤出需要处理的文档
        List<Document> newOrUpdatedDocuments = filterNewOrUpdatedDocuments(allDocuments);

        if (!newOrUpdatedDocuments.isEmpty()) {
            // 批量添加新文档
            vectorStore.add(newOrUpdatedDocuments);

            // 更新文档状态记录
            updateDocumentState(newOrUpdatedDocuments);
        }

        return vectorStore;
    }

    /**
     * 创建文档状态记录表
     */
    private void createDocumentStateTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS document_state (
                doc_id VARCHAR(500) PRIMARY KEY,
                last_modified TIMESTAMP NOT NULL,
                processed_time TIMESTAMP NOT NULL,
                checksum VARCHAR(100)
            )
            """;
        jdbcTemplate.execute(sql);
    }

    /**
     * 过滤出新的或更新的文档
     */
    private List<Document> filterNewOrUpdatedDocuments(List<Document> allDocuments) {
        List<Document> result = new ArrayList<>();

        for (Document doc : allDocuments) {
            String docId = generateDocId(doc);

            // 查询文档是否已处理
            String querySql = "SELECT last_modified, checksum FROM document_state WHERE doc_id = ?";
            List<Map<String, Object>> records = jdbcTemplate.queryForList(querySql, docId);

            if (records.isEmpty()) {
                // 新文档
                result.add(doc);
            } else {
                // 检查文档是否有更新
                Timestamp lastProcessed = (Timestamp) records.get(0).get("last_modified");
                String oldChecksum = (String) records.get(0).get("checksum");
                String newChecksum = calculateChecksum(doc);

                // 如果文档有更新
                if (!newChecksum.equals(oldChecksum)) {
                    result.add(doc);

                    // 可选：从向量存储中删除旧的向量
                    // deleteOldVectors(docId);
                }
            }
        }

        return result;
    }

    /**
     * 生成文档唯一ID
     */
    private String generateDocId(Document document) {
        // 可以根据文档内容、路径、URL等生成唯一ID
        //String source = String.valueOf(document.getMetadata().get("source"));
        String title = String.valueOf(document.getMetadata().get("title"));
        return
//        StringUtils.hasText(source) ? source :
                StringUtils.hasText(title) ? title :
                        String.valueOf(document.hashCode());
    }

    /**
     * 计算文档校验和
     */
    private String calculateChecksum(Document document) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(document.getText().getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to calculate checksum", e);
        }
    }

    /**
     * 更新文档状态
     */
    private void updateDocumentState(List<Document> documents) {
        String sql = """
            INSERT INTO document_state (doc_id, last_modified, processed_time, checksum)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (doc_id) DO UPDATE SET
                last_modified = EXCLUDED.last_modified,
                processed_time = EXCLUDED.processed_time,
                checksum = EXCLUDED.checksum
            """;

        for (Document doc : documents) {
            String docId = generateDocId(doc);
            String checksum = calculateChecksum(doc);

            jdbcTemplate.update(sql,
                    docId,
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    checksum
            );
        }
    }
}
