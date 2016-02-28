package com.target.search.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TotalHitCountCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.target.search.ITextSearch;
import com.target.search.Relevancy;

public class IndexedTextSearch implements ITextSearch {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexedTextSearch.class);

    private String indexPath = "target/index";

    private File docDir;

    @Inject
    public IndexedTextSearch(@Named(value="docDir") File docDir) {
        this.docDir = docDir;
        try {
            index(docDir);
            openIndex();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build document cache", e);
        }
    }

    private void openIndex(String line) throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(reader);
        
        Analyzer analyzer = new StandardAnalyzer();
        String field = "contents";
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(line);
        System.out.println("Searching for: " + query.toString(field));
        
        Collector results = new TotalHitCountCollector();
        searcher.search(query, results);
    }

    @Override
    public List<Relevancy> getRelevancy(String search) throws IOException {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    private void index(File file) throws IOException {
        final Path docDir = file.toPath();
        if (!Files.isReadable(docDir)) {
            throw new IOException("Document directory '" + docDir.toAbsolutePath() + "' does not exist or is not readable, please check the path");
        }

        Date start = new Date();
        LOGGER.debug("Indexing to directory '{}'...", indexPath);

        Directory dir = FSDirectory.open(Paths.get(indexPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        // Add new documents to an existing index:
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

        try (IndexWriter writer = new IndexWriter(dir, iwc)) {
            indexDocs(writer, docDir);
        }

        Date end = new Date();
        LOGGER.debug("{} total milliseconds", end.getTime() - start.getTime());

    }

    /**
     * Indexes the given file using the given writer, or if a directory is
     * given, recurses over files and directories found under the given
     * directory.
     * 
     * @param writer
     *            Writer to the index where the given file/dir info will be
     *            stored
     * @param path
     *            The file to index, or the directory to recurse into to find
     *            files to index
     * @throws IOException
     *             If there is a low-level I/O error
     */
    static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    } catch (IOException ignore) {
                        // don't index files that can't be read.
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }

    /** Indexes a single document */
    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {
            // make a new, empty document
            Document doc = new Document();

            // Add the path of the file as a field named "path". Use a
            // field that is indexed (i.e. searchable), but don't tokenize
            // the field into separate words and don't index term frequency
            // or positional information:
            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);

            // Add the last modified date of the file a field named "modified".
            // Use a LongField that is indexed (i.e. efficiently filterable with
            // NumericRangeFilter). This indexes to milli-second resolution,
            // which
            // is often too fine. You could instead create a number based on
            // year/month/day/hour/minutes/seconds, down the resolution you
            // require.
            // For example the long value 2011021714 would mean
            // February 17, 2011, 2-3 PM.
            doc.add(new LongField("modified", lastModified, Field.Store.NO));

            // Add the contents of the file to a field named "contents". Specify
            // a Reader,
            // so that the text of the file is tokenized and indexed, but not
            // stored.
            // Note that FileReader expects the file to be in UTF-8 encoding.
            // If that's not the case searching for special characters will
            // fail.
            doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

            if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can
                // be there):
                LOGGER.debug("\tAdding {}", file);
                writer.addDocument(doc);
            } else {
                // Existing index (an old copy of this document may have been
                // indexed) so
                // we use updateDocument instead to replace the old one matching
                // the exact
                // path, if present:
                LOGGER.debug("\tUpdating {}", file);
                writer.updateDocument(new Term("path", file.toString()), doc);
            }
        }
    }

}
