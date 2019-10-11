package nl.saxion.concurrency;

public class Reservation {
    int hotelId = -2;
    int roomNr;

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
}
