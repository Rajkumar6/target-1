package org.search.app;

import org.search.ITextSearch;
import org.search.impl.IndexedTextSearch;
import org.search.impl.PatternTextSearch;
import org.search.impl.SimpleTextSearch;

public enum DocumentSearchType {
    STRING(SimpleTextSearch.class), 
    PATTERN(PatternTextSearch.class), 
    INDEXING(IndexedTextSearch.class);

    private Class<? extends ITextSearch> impl;

    private DocumentSearchType(Class<? extends ITextSearch> impl) {
        this.impl = impl;
    }

    public Class<? extends ITextSearch> getImplementation() {
        return impl;
    }
}
