package com.target.search.app;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Scanner;

import com.google.inject.Guice;
import com.target.search.ITextSearch;
import com.target.search.Relevancy;

/**
 * Hello world!
 *
 */
public class DocumentSearchMain {

    public static void main(String[] args) {
        int method;
        String term;
        try (Scanner scan = new Scanner(System.in)) {

            System.out.println("Enter the search term:");
            term = scan.nextLine();
            
            System.out.println("Enter Search Method:\n"
                    + "\t1) String Match\n"
                    + "\t2) Regular Expression\n"
                    + "\t3) Indexed\n"
                    + "? ");
            method = scan.nextInt();
        }

        DocumentSearchType type;
        switch (method) {
        case 1:
            type = DocumentSearchType.STRING;
            break;
        case 2:
            type = DocumentSearchType.PATTERN;
            break;
        case 3:
            type = DocumentSearchType.INDEXING;
            break;
        default:
            System.err.println("Invalid search method: " + method);
            System.exit(-1);
            return;
        }
        DocumentSearchModule module = new DocumentSearchModule();
        module.setType(type);
        
        ITextSearch search = Guice.createInjector(module).getInstance(ITextSearch.class);

        long start = System.currentTimeMillis();
        List<Relevancy> result;
        try {
            result = search.getRelevancy(term);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
            // Just so compiler doesn't complain
            return;
        }
        long end = System.currentTimeMillis();
        System.out.println("Search results: ");
        for (Relevancy r : result) {
            System.out.println(MessageFormat.format("\t{0} - {1} matches", r.getDocument(), r.getMatches()));
        }
        System.out.println(MessageFormat.format("Elapsed time: {0} ms", end - start));
    }
}
