package org.search.impl;

import java.io.File;

import org.search.ITextSearch;

public class PatternTextSearchTest extends AbstractTextSearchTest {

    @Override
    protected ITextSearch createFixture(File docDir) {
        return new PatternTextSearch(docDir);
    }

}
