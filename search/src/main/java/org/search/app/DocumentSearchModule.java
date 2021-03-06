package org.search.app;

import java.io.File;

import org.search.ITextSearch;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;

public class DocumentSearchModule extends AbstractModule {

    private DocumentSearchType type;
    private File docDir = new File(System.getProperty("DOC_DIR", "src/test/resources/sample_text"));

    @Override
    protected void configure() {
        if (type == null) {
            addError(new Message("Missing Document Search Type"));
        } else {
            bind(ITextSearch.class).to(type.getImplementation());
        }
        if (!docDir.exists()) {
            addError(new Message("Document directory does not exist: " + docDir));
        }
        bind(File.class).annotatedWith(Names.named("docDir")).toInstance(docDir);
    }

    public DocumentSearchType getType() {
        return type;
    }

    public void setType(DocumentSearchType type) {
        this.type = type;
    }

    public File getDocDir() {
        return docDir;
    }

    public void setDocDir(File docDir) {
        this.docDir = docDir;
    }

}
