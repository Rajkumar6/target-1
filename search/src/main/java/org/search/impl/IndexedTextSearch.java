package org.search.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.search.ITextSearch;
import org.search.Relevancy;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class IndexedTextSearch implements ITextSearch {

    private final Map<File, Map<String, Integer>> fileMap;

    @Inject
    public IndexedTextSearch(@Named(value = "docDir") File docDir) {
        try {
            fileMap = createFileMap(docDir);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build document cache", e);
        }
    }

    private Map<File, Map<String, Integer>> createFileMap(File docDir) throws IOException {
        Map<File, Map<String, Integer>> retVal = new HashMap<>();
        for (File file : docDir.listFiles()) {
            List<String> words = Files.readAllLines(file.toPath())
                    .stream()
                    .map(line -> Arrays.asList(StringUtils.split(line)))
                    .flatMap(l -> l.stream())
                    .map(word -> word.replaceAll("[^a-zA-Z0-9]", ""))
                    .collect(Collectors.toList());
            Map<String, Integer> cache = new HashMap<>();
            for (String word : words) {
                if (StringUtils.isBlank(word)) {
                    continue;
                }
                Integer value = cache.get(word);
                if (value == null) {
                    cache.put(word, 1);
                } else {
                    cache.put(word, value + 1);
                }
            }

            retVal.put(file, Collections.unmodifiableMap(cache));
        }
        return Collections.unmodifiableMap(retVal);
    }

    @Override
    public List<Relevancy> getRelevancy(String search) throws IOException {
        List<Relevancy> retVal = new ArrayList<>();
        for (Map.Entry<File, Map<String, Integer>> entry : fileMap.entrySet()) {
            Map<String, Integer> cache = fileMap.get(entry.getKey());
            int count = cache.getOrDefault(search, 0);
            retVal.add(new Relevancy(count, entry.getKey().getName()));
        }
        return retVal;
    }

}
