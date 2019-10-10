package nl.saxion.concurrency;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Router;
import nl.saxion.concurrency.Messages.CreateHotel;
import nl.saxion.concurrency.Messages.GetHotelsList;
import nl.saxion.concurrency.Messages.OrderRndRoom;
import scala.concurrent.Future;

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
                    Main.routerBroker = new Router(new RoundRobinRoutingLogic(), Main.routees);
                    hotels.add(createHotel.getHotel());
                })

                .match(GetHotelsList.class, getHotelsList -> getSender().tell(new ListHotelWrapper(hotels), getSelf()))

                .match(OrderRndRoom.class, rndOrder -> {
                    OrderRndRoom orr = new OrderRndRoom();
                    Main.routerBroker.route(orr,getSelf());
                    while (orr.getRoomNr() == -2 && orr.getHotelName() == null){
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    getSender().tell(new Reservation(orr.getHotelName(),orr.getRoomNr()),getSelf());
                })
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
