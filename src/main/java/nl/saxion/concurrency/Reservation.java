package nl.saxion.concurrency;

public class Reservation {
    String hotel;
    int roomNr;

    @Override
    public String toString() {
        if (roomNr ==-1) {
            return "hotel running out of room";
        }
        return "You have reserved room " + roomNr + " in hotel " + hotel;
    }

    public Reservation(String hotel, int roomNr) {
        this.hotel = hotel;
        this.roomNr = roomNr;
    }
}
