package nl.saxion.concurrency;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.remote.RemoteScope;
import akka.routing.*;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.net.InetAddress;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletionStage;


public class Main extends AllDirectives {

    static final List<Routee> routees = new ArrayList<>();
    final Routes routes;
    static Router routerBroker;
    static List<Reservation> waitForConfirmReservation = new ArrayList<>();


    public Main(ActorSystem system, ActorRef broker) {
        routes = new Routes(system, broker);
    }

    public static void main(String[] args) throws Exception {
        String customconfig = getConfig(args);
        Config config = ConfigFactory.parseString(
                customconfig)
                .withFallback(ConfigFactory.load());


        ActorSystem system = ActorSystem.create("TrinhHoangAssignment3", config);

        final ArrayList<Member> memberList = new ArrayList<>();
        Cluster cluster = Cluster.get(system);
        for (Member member : cluster.state().getMembers()){
            if (member.status().equals(MemberStatus.up())){
                memberList.add(member);
            }
        }

        ActorRef broker = system.actorOf(Props.create(Broker.class, system), "broker");

        ActorRef b = system.actorOf(Props.create(Broker.class, system).withDeploy(new Deploy(new RemoteScope(memberList.get(0).address()))));

        int nrOfHotels = 10000;
        for (int i = 0; i < nrOfHotels; i++) {
            Hotel h = new Hotel("hotel" + i, 2);
            Broker.getHotelsList().add(h);
            ActorRef hotelManager = system.actorOf(Props.create(HotelManager.class, h), "hotel" + i);
            routees.add(new ActorRefRoutee(hotelManager));
        }
        routerBroker = new Router(new RoundRobinRoutingLogic(), routees);


        //ROUTING
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        Main app = new Main(system, broker);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("localhost", 8080), materializer);

        System.out.println("Server online at http://localhost:8080/");

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Collections.sort(waitForConfirmReservation, Comparator.comparing(Reservation::getTime));
                boolean overdue = true;
                while (overdue) {
                    if (waitForConfirmReservation.size() > 0
                            && (Instant.now().toEpochMilli() - waitForConfirmReservation.get(0).getTime()) > 2000
                    ) {
                        Broker.getHotelsList().get(waitForConfirmReservation.get(0).getHotelId())
                                .getRooms().get(waitForConfirmReservation.get(0).getRoomNr())
                                .setBooked(false);
                        removeOverdue(0);
                    } else {
                        overdue = false;
                    }
                }
            }
        }, 0, 30000);

    }

    protected Route createRoute() {
        return routes.routes();
    }

    public static synchronized void removeOverdue(int index){
        waitForConfirmReservation.remove(index);
    }

    private static String getConfig(String args[]) {
        String customconfig = "";

        int clusterPort = 0;
        String bindServer = "";
        if (args.length >= 2) {
            clusterPort = Integer.parseInt(args[1]);
        }
        if (args.length >= 1) {
            bindServer = args[0];
        }
        //if clusterPort == 0
        //We are going to use the default config
        //and let AKKA decide wich port to use.

        if (clusterPort > 0)
            customconfig = "\nakka.remote.netty.tcp.port=" + clusterPort;
        if (!bindServer.isEmpty())
            customconfig += "\nakka.remote.netty.tcp.hostname=" + bindServer;

        if (System.getenv("CLUSTER_NAME") != null) {
            String localip = "";
            try {
                localip = InetAddress.getLocalHost().getHostAddress().toString();
            } catch (Exception e) {

            }
            customconfig += "\nakka.remote.netty.tcp.bind-hostname=" + localip;
            customconfig += "\nakka.remote.netty.tcp.bind-port=" + clusterPort;
            System.out.println("Starting server behing NAT");
        }

        if (System.getenv("SEED1") != null) {
            customconfig += " \nakka.cluster.seed-nodes = [\"" + System.getenv("SEED1")
                    + "\",\"" + System.getenv("SEED2") + "\"]";
            customconfig += "\nakka.remote.netty.tcp.hostname=" + System.getenv("HOST_NAME");
        }
        return customconfig;
    }

}