package nl.saxion.concurrency;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.util.Timeout;
import nl.saxion.concurrency.Messages.CreateHotel;
import nl.saxion.concurrency.Messages.GetHotelsList;
import nl.saxion.concurrency.Messages.OrderRndRoom;
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
                path("hotel", () -> route(createHotel())),
                path("order", () -> route(orderRoom())));
    }

    private Route orderRoom() {
        return get(()-> {
            Future<Object> reservation  = Patterns.ask(broker,new OrderRndRoom(),timeout);
            while(!reservation.isCompleted()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return completeOK(reservation.toString(),Jackson.marshaller());
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
}
