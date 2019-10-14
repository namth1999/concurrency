package nl.saxion.concurrency;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.util.Timeout;
import nl.saxion.concurrency.Messages.*;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

public class Routes extends AllDirectives {
    private ActorSystem system;
    final Timeout timeout = Timeout.durationToTimeout(FiniteDuration.apply(5,
            TimeUnit.SECONDS));
    private ActorRef broker;

    public Routes(ActorSystem system, ActorRef broker) {
        this.system = system;
        this.broker = broker;
    }

    public Route routes() {
        return route(path("hotels", () -> route(getHotelsList())),
                path("reservations", () -> route(getReservation())),
                path("hotel", () -> route(createHotel())),
                path("orders", () -> concat(pathEnd(() -> route(orderRoom())))),
                orderSpecificHotel(),
                confirm(),
                removeReservation()
        );
    }

    private Route getReservation() {
        return get(() -> {
            Future<Object> reservationsList = Patterns.ask(broker, new GetReservations(), timeout);
            return completeOKWithFuture(reservationsList, Jackson.marshaller());
        });
    }

    private Route removeReservation() {
        return pathPrefix("cancel",
                () -> parameter("hotel", hParam
                        -> parameter("room", rParam
                                -> delete(() -> {
                            int hotel = Integer.parseInt(hParam);
                            int room = Integer.parseInt(rParam);
                            Future<Object> reply = Patterns.ask(broker, new CancelReservation(hotel, room), timeout);

                            while (!reply.isCompleted()) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            return complete(reply.toString());
                        })
                )));
    }

    private Route confirm() {
        return pathPrefix("confirm",
                () -> parameter("hotel", hParam
                        -> parameter("room", rParam
                                -> get(() -> {
                            int hotel = Integer.parseInt(hParam);
                            int room = Integer.parseInt(rParam);
                            Future<Object> reply = Patterns.ask(broker, new ConfirmReservation(hotel, room), timeout);
                            while (!reply.isCompleted()) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            return complete(reply.toString());
                        })
                )));
    }

    private Route orderRoom() {
        return get(() -> {
            Future<Object> reservation = Patterns.ask(broker, new OrderRndRoom(), timeout);
            while (!reservation.isCompleted()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return complete(reservation.toString());
        });
    }

    private Route createHotel() {
        return post(() ->
                entity(Jackson.unmarshaller(Hotel.class),
                        hotel -> {
                            Patterns.ask(broker, new CreateHotel(hotel), timeout);
                            return complete("Hotel created: " + hotel.getName());
                        }));
    }

    private Route getHotelsList() {
        return get(() -> {
            Future<Object> hotelList = Patterns.ask(broker, new GetHotelsList(), timeout);
            return completeOKWithFuture(hotelList, Jackson.marshaller());
        });
    }

    private Route orderSpecificHotel() {
        return concat(
                pathPrefix("order", () -> concat(
                        get(() ->
                                path(PathMatchers.integerSegment(), (ID) -> {
                                            Integer id = Integer.valueOf(ID);
                                            if (Broker.getHotelsList().size() <= id) {
                                                return complete("No hotels exist with such id");
                                            }
                                            Future<Object> reservation =
                                                    Patterns.ask(broker, new OrderSpecificHotel(id), timeout);
                                            while (!reservation.isCompleted()) {
                                                try {
                                                    Thread.sleep(5);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            return complete(reservation.toString());
                                        }
                                ))
                )));
    }


}
