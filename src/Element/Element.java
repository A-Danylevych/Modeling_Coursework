package Element;

import Random.IRandom;

public class Element {
    private String name;
    private double timeNext;
    private IRandom distribution;
    private int quantity;
    private double timeCurrent;
    private int state;
    private Element nextElement;
    private static int nextId=0;
    private int id;

    public Element(){
        timeNext = 0;
        timeCurrent = timeNext;
        state=0;
        nextElement=null;
        id = nextId;
        nextId++;
        name = "element"+id;
    }

    public double getDelay() {
        return distribution.Random();
    }


    public void setDistribution(IRandom distribution) {
        this.distribution = distribution;
    }


    public int getQuantity() {
        return quantity;
    }

    public double getTimeCurrent() {
        return timeCurrent;
    }

    public void setTimeCurrent(double timeCurrent) {
        this.timeCurrent = timeCurrent;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Element getNextElement() {
        return nextElement;
    }

    public void setNextElement(Element nextElement) {
        this.nextElement = nextElement;
    }

    public void inAct() {

    }
    public void outAct(){
        quantity++;
    }

    public double getTimeNext() {
        return timeNext;
    }


    public void setTimeNext(double timeNext) {
        this.timeNext = timeNext;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void printResult(){
        System.out.println(getName()+ "  quantity = "+ quantity);
    }

    public void printInfo(){
        System.out.println(getName()+ " state= " +state+
                " quantity = "+ quantity+
                " time next= "+ timeNext);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void doStatistics(double delta){

    }
}

