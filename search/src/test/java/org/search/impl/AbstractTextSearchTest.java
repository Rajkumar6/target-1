package org.search.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.search.ITextSearch;
import org.search.Relevancy;

public abstract class AbstractTextSearchTest {
    
    private ITextSearch fixture;
    
    @Before
    public void setup() {
        fixture = createFixture(new File("src/test/resources/sample_text"));
    }

    protected abstract ITextSearch createFixture(File docDir);

    @Test
    public void testGetRelevancy() throws IOException {
        List<Relevancy> expected = Arrays.asList(
                new Relevancy(0, "hitchhikers.txt"),
                new Relevancy(1, "simple.txt"),
                new Relevancy(0, "french_armed_forces.txt"),
                new Relevancy(0, "warp_drive.txt"));
        List<Relevancy> result = fixture.getRelevancy("hello");
        
        assertEquals(expected, result);
    }
    
    @Test
    public void testGetRelevancy2() throws IOException {
        List<Relevancy> expected = Arrays.asList(
                new Relevancy(24, "hitchhikers.txt"),
                new Relevancy(1, "simple.txt"),
                new Relevancy(59, "french_armed_forces.txt"),
                new Relevancy(9, "warp_drive.txt"));
        List<Relevancy> result = fixture.getRelevancy("the");
        
        assertEquals(expected, result);
    }
    
    
    @Test
    public void testGetRelevancy3() throws IOException {
        List<Relevancy> expected = Arrays.asList(
                new Relevancy(0, "hitchhikers.txt"),
                new Relevancy(0, "simple.txt"),
                new Relevancy(0, "french_armed_forces.txt"),
                new Relevancy(0, "warp_drive.txt"));
        List<Relevancy> result = fixture.getRelevancy("***");
        
        assertEquals(expected, result);
    }

}
