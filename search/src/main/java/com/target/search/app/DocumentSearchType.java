package com.target.search.app;

import com.target.search.ITextSearch;
import com.target.search.impl.IndexedTextSearch;
import com.target.search.impl.PatternTextSearch;
import com.target.search.impl.SimpleTextSearch;

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
