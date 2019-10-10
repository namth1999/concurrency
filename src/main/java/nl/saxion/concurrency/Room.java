package nl.saxion.concurrency;

public class Room {
    private int roomNr;
    private boolean booked;

    public void setRoomNr(int roomNr) {
        this.roomNr = roomNr;
    }

    public Room(int roomNr, boolean booked) {
        this.roomNr = roomNr;
        this.booked = booked;
    }

    public Room() {
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
