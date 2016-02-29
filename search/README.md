# Document Search

The goal of this exercise is to create a working program to search a set of documents 
for the given search term or phrase (single token), and return results in order of relevance. 

Relevancy is defined as number of times the exact term or phrase appears in the document. 
Create three methods for searching the documents: 
  * Simple string matching
  * Text search using regular expressions
  * Preprocess the content and then search the index

Prompts the user to enter a search term and search method, execute the search, and return results. 

## Example Usage
```bash
mvn clean install
mvn exec:java [-DDOC_DIR={path_to_documents}]
```

## Stats
1. Run a performance test that does 2M searches with random search terms, and measures execution time.
  * Created a runner to run 2M queries using terms from the documents and the American dictionary
  * Results available in results.xlsx
2. Which approach is fastest? 
  * Indexed cache
3. Why?
  * The has a O(1) lookup time for each request where as the String and Pattern matcher have O(n) lookup time since each time they are parsing the entire file contents.  The pattern matcher also takes a significant penalty when creating the literal pattern.

Provide some thoughts on what you would do on the software or hardware side to make this program scale to handle massive content and/or very large request volume (5000 requests/second or more).

The indexed cache is significantly faster than the other approaches.  However, my implementation is primitive at best.  I would rather use other existing indexing libraries such as Lucene or MG4J.  These libraries provide far greater capabilities and are proven.

In order to handle the high loads consider splitting the loads amongst several machines.  Perhaps a REST service with a load balancing middleware.  The index could be placed within a high speed NAS for quick access amongst the various processing nodes.

There are multiple factors to consider when defining the necessary hardware requirements.  If the document set remained small you could easily handle 5000 request per second on a server as the results showed in the index scenario I was able to handle nearly 2M requests in under a second.  But as the size of the document set increased memory requirements would grow.

## Follow On Questions
1. Case sensitive?
1. Sub sequences?
1. Multi-word phrases?
1. Regular Expression matching.
1. How does document size and number of documents effect processing performance?