package org.search.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.search.ITextSearch;
import org.search.Relevancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;

public class StatsRunnerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsRunnerMain.class);

    private static class Result {
        int words;
        Map<DocumentSearchType, Long> time = new EnumMap<>(DocumentSearchType.class);
    }

    public static void main(String[] args) {
        List<String> words;
        try {
            words = Files.readAllLines(Paths.get("src/test/resources/wordList.txt"));
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(-1);
            return;
        }
        int runSize = 2000000;
        int stepSize = 100000;
        List<Result> results = new ArrayList<>();
        for (int i = 0; i <= runSize / stepSize; i++) {
            Result r = new Result();
            r.words = i * stepSize;
            results.add(r);
        }

        for (DocumentSearchType type : DocumentSearchType.values()) {
            LOGGER.debug("Running type: {}", type);
            int stepIndex = 0;
            DocumentSearchModule module = new DocumentSearchModule();
            module.setType(type);
            ITextSearch search = Guice.createInjector(module).getInstance(ITextSearch.class);
            
            long start = System.currentTimeMillis();
            for (int i = 0; i <= runSize; i++) {
                try {
                    search.getRelevancy(words.get(i % words.size()));
                    if (i % stepSize == 0) {
                        long end = System.currentTimeMillis();
                        results.get(stepIndex).time.put(type, (end - start));
                        stepIndex++;
                        LOGGER.debug("Running type: {} {}", type, i);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                    // Just so compiler doesn't complain
                    return;
                }
            }
        }
        
        LOGGER.debug("Writting output...");
        
        List<String> lines = results.stream().map(r -> 
            MessageFormat.format("{0,number,#},{1,number,#},{2,number,#},{3,number,#}", 
                    r.words, 
                    r.time.get(DocumentSearchType.STRING),
                    r.time.get(DocumentSearchType.PATTERN),
                    r.time.get(DocumentSearchType.INDEXING))).collect(Collectors.toList());
        lines.add(0, "Words,STRING,PATTERN,INDEXING");
        try {
            Files.write(Paths.get("target/results.csv"), lines);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
            // Just so compiler doesn't complain
            return;
        }

    }

}
