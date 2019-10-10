package nl.saxion.concurrency;

import akka.actor.AbstractActor;

public class HotelManager extends AbstractActor {
    private Hotel hotel;

    public HotelManager(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
