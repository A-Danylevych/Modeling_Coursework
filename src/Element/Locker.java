package Element;

public class Locker {
    private ProcessTaker taker;
    private ProcessGiver giver;
    private int available;
    private final int maxAvailable;
    public Locker(int available){
        this(available, Integer.MAX_VALUE);
    }
    public Locker(int available, int maxAvailable){
        this.available = available;
        this.maxAvailable = maxAvailable;
    }
    public void give(int give){
        this.available += give;
        this.taker.give();
    }
    public void take(int take){
        this.available -= take;
        this.giver.take();
    }
    public boolean canTake(int take){
        return this.available - take >= 0;
    }
    public boolean canGive(int give){
        return this.available + give <= this.maxAvailable;
    }

    public void setTaker(ProcessTaker tacker) {
        this.taker = tacker;
    }

    public void setGiver(ProcessGiver giver) {
        this.giver = giver;
    }
    public double getUsage(){
        return (double) this.available/this.maxAvailable;
    }
    public int getAvailable(){
        return this.available;
    }
    public int getMaxAvailable(){
        return this.maxAvailable;
    }
    public int getUsed(){
        return this.maxAvailable-this.available;
    }
}
