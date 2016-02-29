package org.search.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordListBuilderMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordListBuilderMain.class);

    public static void main(String[] args) {
        LOGGER.debug("Start");
        Set<String> allWords = new HashSet<>();
        try {
            
            for (File file : new File("src/test/resources/sample_text").listFiles()) {
                Set<String> words = Files.readAllLines(file.toPath())
                        .stream()
                        .map(line -> Arrays.asList(StringUtils.split(line)))
                        .flatMap(l -> l.stream())
                        .collect(Collectors.toSet());
                allWords.addAll(words);
            }
            List<String> words = Files.readAllLines(Paths.get("/usr/share/dict/american-english"));
            allWords.addAll(words);
            
            List<String> finalList = new ArrayList<>(allWords);
            Collections.sort(finalList);
            
            Files.write(Paths.get("src/test/resources/wordList.txt"), finalList);
        } catch (IOException e) {
            LOGGER.error("Error while writing out word list", e);
        }
        LOGGER.debug("Done");
    }

}
