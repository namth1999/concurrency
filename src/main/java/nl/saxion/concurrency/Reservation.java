package nl.saxion.concurrency;

public class Reservation {
    String hotel;
    int roomNr;

    @Override
    public String toString() {
        return "You have reserved room " + roomNr + " in hotel " + hotel;
    }

    public Reservation(String hotel, int roomNr) {
        this.hotel = hotel;
        this.roomNr = roomNr;
    }
}
