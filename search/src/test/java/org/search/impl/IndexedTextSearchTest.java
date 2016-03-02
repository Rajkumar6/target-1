package org.search.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.search.ITextSearch;
import org.search.Relevancy;

public class IndexedTextSearchTest extends AbstractTextSearchTest {

    @Override
    protected ITextSearch createFixture(File docDir) {
        return new IndexedTextSearch(docDir);
    }

    @Test
    public void testGetRelevancy2() throws IOException {
        List<Relevancy> expected = Arrays.asList(
                new Relevancy(21, "hitchhikers.txt"),
                new Relevancy(2, "simple.txt"),
                new Relevancy(57, "french_armed_forces.txt"),
                new Relevancy(6, "warp_drive.txt"));
        List<Relevancy> result = fixture.getRelevancy("the");

        assertEquals(expected, result);
    }
}
