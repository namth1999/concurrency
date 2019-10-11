package nl.saxion.concurrency;

public class Room {
    private int roomNr;
    private boolean booked;
    private boolean staked;

    public Room(int roomNr, boolean booked, boolean staked) {
        this.roomNr = roomNr;
        this.booked = booked;
        this.staked = staked;
    }

    public Room() {
    }

    public void setRoomNr(int roomNr) {
        this.roomNr = roomNr;
    }

    public int getRoomNr() {
        return roomNr;
    }

    public boolean isBooked() {
        return booked;
    }

    public boolean isStaked() {
        return staked;
    }

    public void setStaked(boolean staked) {
        this.staked = staked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

}
