package org.search.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.search.ITextSearch;
import org.search.Relevancy;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SimpleTextSearch implements ITextSearch {

 private Map<File, String> fileMap = new HashMap<>();
    
    private File docDir;
    
    @Inject
    public SimpleTextSearch(@Named(value="docDir") File docDir) {
        this.docDir = docDir;
        try {
            readFiles();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build document cache", e);
        }
    }
    

    private void readFiles() throws IOException {
        Collection<File> files = FileUtils.listFiles(docDir, null, false);
        for (File file : files) {
            fileMap.put(file, FileUtils.readFileToString(file));
        }
    }


    @Override
    public List<Relevancy> getRelevancy(String search) throws IOException {
        List<Relevancy> retVal = new ArrayList<>();
        for (Map.Entry<File, String> entry : fileMap.entrySet()) {
            String content = fileMap.get(entry.getKey());
            int count = StringUtils.countMatches(content, search);
            retVal.add(new Relevancy(count, entry.getKey().getName()));
        }
        return retVal;
    }

}
