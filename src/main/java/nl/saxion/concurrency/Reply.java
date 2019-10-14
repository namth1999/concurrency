package nl.saxion.concurrency;

public class Reply {
    private String rep;

    public String getRep() {
        return rep;
    }

    public void setRep(String rep) {
        this.rep = rep;
    }

    @Override
    public String toString() {
        return rep;
    }
}
