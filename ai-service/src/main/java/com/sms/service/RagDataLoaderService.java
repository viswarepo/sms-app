package com.ai.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagDataLoaderService {

    private final VectorStore vectorStore;

    @Value("classpath:data/knowledge-base.txt")
    private Resource localDocument;

    public RagDataLoaderService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void populateVectorStore() {
        // 1. Read document
        TextReader textReader = new TextReader(localDocument);
        List<Document> documents = textReader.get();

        // 2. Fragment documents into smaller tokens
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.apply(documents);

        // 3. Convert to embeddings and write to vector store
        this.vectorStore.accept(splitDocuments);
    }
}

