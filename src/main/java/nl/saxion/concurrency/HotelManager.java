package nl.saxion.concurrency;

import akka.actor.AbstractActor;
import nl.saxion.concurrency.Messages.OrderRndRoom;

public class HotelManager extends AbstractActor {
    private Hotel hotel;

    public HotelManager(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderRndRoom.class, order -> {
                    order.setRoomNr(hotel.orderRoom());
                    order.setHotelName(hotel.getName());
                })
                .build();
    }
}
