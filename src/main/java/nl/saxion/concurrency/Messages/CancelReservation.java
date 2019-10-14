package nl.saxion.concurrency.Messages;

public final class CancelReservation {
    private int hotelId;
    private int roomNr;

    public CancelReservation(int hotelId, int roomNr) {
        this.hotelId = hotelId;
        this.roomNr = roomNr;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public int getRoomNr() {
        return roomNr;
    }

    public void setRoomNr(int roomNr) {
        this.roomNr = roomNr;
    }
}
