package com.target.search;

public class Relevancy {
    private final int matches;
    private final String document;

    public Relevancy(int matches, String document) {
        super();
        this.matches = matches;
        this.document = document;
    }

    public int getMatches() {
        return matches;
    }

    public String getDocument() {
        return document;
    }

}
