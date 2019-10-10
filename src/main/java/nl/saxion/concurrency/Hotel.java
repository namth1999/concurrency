package nl.saxion.concurrency;

import java.util.ArrayList;

public class Hotel{
    private String name;
    private ArrayList<Room> rooms;
    private int nrRooms;

    public Hotel(String name, int nrRooms) {
        this.nrRooms = nrRooms;
        rooms = new ArrayList<>();
        this.name = name;
        for (int i=0;i<nrRooms;i++){
            Room r = new Room(i, false);
            rooms.add(r);
        }
    }

    public Hotel() {
        this.name = "Nam hotel";
        this.rooms = new ArrayList<>();
        this.nrRooms = 0;
    }

    public Hotel(String name, int nrRooms, ArrayList<Room> rooms){
        this.nrRooms = nrRooms;
        this.name = name;
        this.rooms = rooms;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public void setNrRooms(int nrRooms) {
        this.nrRooms = nrRooms;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public int getNrRooms() {
        return nrRooms;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "name='" + name + '\'' +
                ", rooms=" + rooms +
                '}';
    }

    public int orderRoom(){
        int nrRoom = -1;
        for (int i=0;i<nrRooms;i++){
            if (!rooms.get(i).isBooked()){
                nrRoom = i;
                rooms.get(i).setBooked(true);
                break;
            }
        }
        return nrRoom;
    }
}
