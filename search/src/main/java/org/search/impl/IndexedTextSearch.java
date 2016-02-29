package org.search.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.search.ITextSearch;
import org.search.Relevancy;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class IndexedTextSearch implements ITextSearch {

    private Map<File, Map<String, Integer>> fileMap = new HashMap<>();

    @Inject
    public IndexedTextSearch(@Named(value = "docDir") File docDir) {
        try {
            readFiles(docDir);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build document cache", e);
        }
    }

    private void readFiles(File docDir) throws IOException {
        for (File file : docDir.listFiles()) {
            String content = FileUtils.readFileToString(file);
            String[] words = StringUtils.split(content);
            Map<String, Integer> cache = new HashMap<>();
            for (String word : words) {
                Integer value = cache.get(word);
                if (value == null) {
                    cache.put(word, 1);
                } else {
                    cache.put(word, value + 1);
                }
            }

            fileMap.put(file, cache);
        }
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
