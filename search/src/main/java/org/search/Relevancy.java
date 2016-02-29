package org.search;

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

    @Override
    public String toString() {
        return "Relevancy [matches=" + matches + ", document=" + document + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((document == null) ? 0 : document.hashCode());
        result = prime * result + matches;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Relevancy other = (Relevancy) obj;
        if (document == null) {
            if (other.document != null)
                return false;
        } else if (!document.equals(other.document))
            return false;
        if (matches != other.matches)
            return false;
        return true;
    }
}
