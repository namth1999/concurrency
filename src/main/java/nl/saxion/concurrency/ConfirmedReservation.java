package nl.saxion.concurrency;

public class ConfirmedReservation {
    private int id;
    private int roomNr;
    private int hotelId;
    private static int lastAssignedId = 0;

    public ConfirmedReservation(int hotel, int room) {
        lastAssignedId++;
        this.id = lastAssignedId;
        this.hotelId = hotel;
        this.roomNr = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomNr() {
        return roomNr;
    }

    public void setRoomNr(int roomNr) {
        this.roomNr = roomNr;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }
}
