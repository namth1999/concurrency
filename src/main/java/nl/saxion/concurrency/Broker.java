package nl.saxion.concurrency;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Router;
import nl.saxion.concurrency.Messages.*;

import java.util.ArrayList;
import java.util.List;


public class Broker extends AbstractActor {
    private static List<Hotel> hotels = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
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
                    Main.routerBroker.route(orr, getSelf());
                    while (orr.getRoomNr() == -2 && orr.getHotelId() == -2) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Reservation reservation = new Reservation(orr.getHotelId(), orr.getRoomNr());
                    Main.waitForConfirmReservation.add(reservation);
                    getSender().tell(reservation, getSelf());
                })

                .match(OrderSpecificHotel.class, sOrder -> {
                    Main.routerBroker.route(sOrder, getSelf());

                    while (sOrder.getRoomNr() == -2) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    Reservation reservation = new Reservation(sOrder.getHotelId(), sOrder.getRoomNr());
                    Main.waitForConfirmReservation.add(reservation);
                    getSender().tell(reservation, getSelf());
                })

                .match(ConfirmReservation.class, confReservation -> {
                    Reply rep = new Reply();
                    List<Reservation> reservations = Main.waitForConfirmReservation;
                    int index = -1;

                    for (int i = 0; i < reservations.size(); i++) {
                        if (reservations.get(i).getHotelId() == confReservation.getHotelId()
                                && reservations.get(i).getRoomNr() == confReservation.getRoomNr()) {
                            index = i;
                        }
                    }

                    if (index == -1) {
                        rep.setRep("Can't confirm. No reservation found on such room");
                    } else {
                        hotels.get(confReservation.getHotelId())
                                .getRooms().get(confReservation.getRoomNr()).setStaked(true);
                        rep.setRep("Successfully confirm reservation on "
                                + hotels.get(confReservation.getHotelId()).getName()
                                + " room " + confReservation.getRoomNr());
                        Broker.reservations.add(reservations.get(index));
                        Main.removeOverdue(index);
                    }
                    getSender().tell(rep, getSelf());
                })

                .match(CancelReservation.class, cancelReservation -> {
                    Reply rep = new Reply();
                    int index = -1;
                    for (int i = 0; i < reservations.size(); i++) {
                        if (reservations.get(i).getHotelId() == cancelReservation.getHotelId()
                                && reservations.get(i).getRoomNr() == cancelReservation.getRoomNr()) {
                            index = i;
                        }
                    }

                    if (index == -1) {
                        rep.setRep("No reservation found on such room");
                    } else {
                        hotels.get(cancelReservation.getHotelId())
                                .getRooms().get(cancelReservation.getRoomNr()).setStaked(false);
                        hotels.get(cancelReservation.getHotelId())
                                .getRooms().get(cancelReservation.getRoomNr()).setBooked(false);
                        reservations.remove(index);
                        rep.setRep("Successfully removed the reservation");
                    }
                    getSender().tell(rep, getSelf());
                })

                .match(GetReservations.class,getReservations -> {
                    getSender().tell(new ListReservationWrapper(reservations), getSelf());
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

    public static class ListReservationWrapper {
        private List<Reservation> reservations;

        public List<Reservation> getReservations() {
            return reservations;
        }

        public ListReservationWrapper(List<Reservation> reservations) {
            this.reservations = reservations;
        }
    }
}
