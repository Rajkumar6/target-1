package org.search;

import java.io.IOException;
import java.util.List;

public interface ITextSearch {
    
    List<Relevancy> getRelevancy(String search) throws IOException;
}
