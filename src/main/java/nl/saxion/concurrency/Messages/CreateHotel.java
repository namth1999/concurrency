package nl.saxion.concurrency.Messages;

import nl.saxion.concurrency.Hotel;

public final class CreateHotel {
    private final Hotel hotel;

    public CreateHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Hotel getHotel() {
        return hotel;
    }
}
