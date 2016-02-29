package org.search.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.FileUtils;
import org.search.ITextSearch;
import org.search.Relevancy;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class PatternTextSearch implements ITextSearch {

    private Map<File, String> fileMap = new HashMap<>();

    private File docDir;

    @Inject
    public PatternTextSearch(@Named(value = "docDir") File docDir) {
        this.docDir = docDir;
        try {
            readFiles();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build document cache", e);
        }
    }

    private void readFiles() throws IOException {
        for (File file : docDir.listFiles()) {
            fileMap.put(file, FileUtils.readFileToString(file));
        }
    }

    @Override
    public List<Relevancy> getRelevancy(String search) throws IOException {
        List<Relevancy> retVal = new ArrayList<>();
        for (Map.Entry<File, String> entry : fileMap.entrySet()) {
            String content = fileMap.get(entry.getKey());
            Pattern pattern = Pattern.compile(search, Pattern.LITERAL);

            Matcher matcher = pattern.matcher(content);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            retVal.add(new Relevancy(count, entry.getKey().getName()));
        }
        return retVal;
    }

}
