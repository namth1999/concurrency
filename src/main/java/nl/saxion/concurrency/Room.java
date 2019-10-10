package nl.saxion.concurrency;

public class Room {
    private final int roomNr;
    private boolean booked;

    public Room(int roomNr, boolean booked) {
        this.roomNr = roomNr;
        this.booked = booked;
    }

    public int getRoomNr() {
        return roomNr;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

}
