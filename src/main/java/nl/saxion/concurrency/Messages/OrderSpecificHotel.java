package nl.saxion.concurrency.Messages;

public final class OrderSpecificHotel {
    private int hotelId;
    private int roomNr = -2;

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

    public OrderSpecificHotel(int hotelId) {
        this.hotelId = hotelId;
    }
}
