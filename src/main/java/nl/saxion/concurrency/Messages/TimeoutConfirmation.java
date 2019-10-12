package nl.saxion.concurrency.Messages;

public class TimeoutConfirmation {
    int hotelId;
    int roomNr;

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

    public TimeoutConfirmation(int hotelId, int roomNr) {
        this.hotelId = hotelId;
        this.roomNr = roomNr;
    }
}
