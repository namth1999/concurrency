package nl.saxion.concurrency;

import akka.actor.AbstractActor;
import nl.saxion.concurrency.Messages.OrderRndRoom;
import nl.saxion.concurrency.Messages.OrderSpecificHotel;

public class HotelManager extends AbstractActor {
    private Hotel hotel;

    public HotelManager(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderRndRoom.class, order -> {
                    int id = -2;
                    for (int i=0;i<Broker.getHotelsList().size();i++){
                        if (hotel.getName().equals(Broker.getHotelsList().get(i).getName())){
                            id = i;
                        }
                    }
                    order.setRoomNr(hotel.orderRoom());
                    order.setHotelId(id);
                    if (order.getRoomNr() == -1) {
                        Main.routerBroker.route(order,getSelf());
                    }
                })

                .match(OrderSpecificHotel.class, sOrder -> {
                    if (Broker.getHotelsList().get(sOrder.getHotelId()).getName().equals(hotel.getName())){
                        if (hotel.roomAvailable()){
                            sOrder.setRoomNr(hotel.orderRoom());
                        } else {
                            sOrder.setRoomNr(-1);
                        }

                    } else {
                        Main.routerBroker.route(sOrder,getSelf());
                    }
                })

                .build();
    }
}
