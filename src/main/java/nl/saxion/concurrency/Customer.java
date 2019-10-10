package nl.saxion.concurrency;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Customer extends AbstractActor {

    //create a logger
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorRef mySalesPerson = null;

    public Customer() {

    }

    @Override
    public void preStart() throws Exception {
        //todo: send a message to all SalesPersons.
        //todo:the first salesperson to respons will be our salesperson and we will send
        //todo:ten messages to him.
        getContext().getSystem().getEventStream().publish(new GetSalePerson(getSelf()));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, msg -> {
            log.debug(msg);
        })
                .match(ActorRef.class, actor -> {
                    //todo finish the code
                    if (mySalesPerson==null){
                        mySalesPerson = actor;
                        for (int i=0;i<5;i++){
                            mySalesPerson.tell("I want to buy something", getSelf());
                        }
                    }
                })
                .build();
    }

    public class GetSalePerson {
        ActorRef customer;
        public GetSalePerson(ActorRef ref) {
            this.customer = ref;
        }
    }
}