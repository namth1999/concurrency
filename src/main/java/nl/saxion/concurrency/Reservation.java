package nl.saxion.concurrency;

import java.time.Instant;

public class Reservation {
    int hotelId = -2;
    int roomNr;
    long time = Instant.now().toEpochMilli();

    @Override
    public String toString() {
        if (roomNr ==-1) {
            return "Running out of room";
        }
        return "You have booked room " + roomNr + " in hotel " + Broker.getHotelsList().get(hotelId).getName()
                + ". Please confirm your reservation in 60s or it will be canceled";
    }

    public Reservation(int hotelId, int roomNr) {
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

    public long getTime() {
        return time;
    }
}
