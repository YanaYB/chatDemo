package com.example.chatDemo.service;

import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class DocService implements CommandLineRunner {
    private final VectorStore vectorStore;
    @Value("classpath:/files/example.pdf")
    private  Resource resource;

    public DocService(VectorStore vectorStore){
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception{
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        TextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(150)
                .withMinChunkSizeChars(30)
                .withMinChunkLengthToEmbed(5)
                .withMaxNumChunks(2000)
                .withKeepSeparator(true)
                .build();
        vectorStore.accept(textSplitter.apply(reader.get()));

    }
}
