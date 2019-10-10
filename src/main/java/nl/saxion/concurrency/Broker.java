package nl.saxion.concurrency;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import nl.saxion.concurrency.Messages.CreateHotel;
import nl.saxion.concurrency.Messages.GetHotelsList;

import java.util.ArrayList;
import java.util.List;


public class Broker extends AbstractActor {
    private static List<Hotel> hotels = new ArrayList<>();
    private ActorSystem system;

    public Broker(ActorSystem system) {
        this.system = system;
    }

    public static List<Hotel> getHotelsList() {
        return hotels;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateHotel.class, createHotel -> {
                    ActorRef hotelManager = system.actorOf(Props.create(HotelManager.class, createHotel.getHotel()), "hotel" + Main.routees.size());
                    Main.routees.add(new ActorRefRoutee(hotelManager));
                    hotels.add(createHotel.getHotel());
                })
                .match(GetHotelsList.class, getHotelsList -> getSender().tell(new ListHotelWrapper(hotels), getSelf()))
                .build();
    }

    public static class ListHotelWrapper {
        private List<Hotel> hotels;

        public ListHotelWrapper(List<Hotel> hotels) {
            this.hotels = hotels;
        }

        public List<Hotel> getHotels() {
            return hotels;
        }
    }
}
