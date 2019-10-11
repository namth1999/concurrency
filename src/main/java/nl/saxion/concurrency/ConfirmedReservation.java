package nl.saxion.concurrency;

public class ConfirmedReservation {
    private int id;
    private Room room;
    private static int lastAssignedId = 0;

    public ConfirmedReservation(Room room) {
        lastAssignedId++;
        this.id = lastAssignedId;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
