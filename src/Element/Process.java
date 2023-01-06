package Element;

import Random.IRandom;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Process extends Element {
    private boolean waitToIN;
    protected int maxState;

    private final Map<Integer, Double> meanQueueById;
    private final Map<Integer, Double> meanLoadById;
    protected final Map<Integer, Integer> stateById;
    protected final Map<Integer, Integer> changedById;
    protected final Map<Integer, Integer> failureById;
    protected final Map<Integer, Integer> completedById;
    protected final Map<Integer, Integer> inById;
    protected Queue queue;
    private NextProcessManager nextProcessManager;
    protected Map<Integer,ChangeProcessManager> changeProcessManagerMap;
    protected final PriorityQueue<Pair<Double, Integer>> nextTimeQueue;
    private Map<Integer, IRandom> typeDistribution;
    protected Map<Integer, Integer> outMap;
    public Process() {
        meanQueueById = new HashMap<>();
        stateById = new HashMap<>();
        meanLoadById = new HashMap<>();
        changedById = new HashMap<>();
        failureById =new HashMap<>();
        inById = new HashMap<>();
        maxState = 1;
        nextTimeQueue = new PriorityQueue<>(new PairComparator());
        completedById = new HashMap<>();
    }
    public void getNextTimes(int number, int id){
        for (int i =0; i<number; i++){
            inAct(id);
        }
    }
    public void setInfiniteQueue(int index){
        setInfiniteQueue(index, index);
    }

    public void setInfiniteQueue(int firstIndex, int lastIndex){
        queue = new Queue(Integer.MAX_VALUE,true, firstIndex, lastIndex, true);
    }
    public void setZeroQueue(int index){
        setZeroQueue(index, index);
    }

    public void setZeroQueue(int firstIndex, int lastIndex){
        queue = new Queue(0,true, firstIndex, lastIndex, true);
    }
    @Override
    public double getTimeNext() {
        if(nextTimeQueue.isEmpty()){
            setTimeNext(Double.MAX_VALUE);
            return super.getTimeNext();
        }
        return nextTimeQueue.peek().getKey();
    }

    public int getCurrentId(){
        return nextTimeQueue.peek().getValue();
    }

    public void setTimeNext(double timeNext, int id) {
        nextTimeQueue.add(new ImmutablePair<>(timeNext, id));
        super.setTimeNext(nextTimeQueue.peek().getKey());
    }
    public void inAct(int queueId) {
        IncrementOrCreate(inById, queueId);
        if (super.getState() < maxState) {
            var timeNext = super.getTimeCurrent() + getDelay(queueId);
            IncrementOrCreate(stateById, queueId);
            super.setState(getState()+1);
            setTimeNext(timeNext, queueId);
        } else {
            if(!queue.TryAddToQueue(queueId)){
              IncrementOrCreate(failureById, queueId);
            }
        }
    }

    @Override
    public void outAct() {
        Process next = (Process) getNextElement();

        if (next != null && next.isWaitToIN() && !next.canIn()){
            setTimeNext(next.getTimeNext(), getCurrentId());
            nextTimeQueue.poll();
            return;
        }
        int state = getState()-1;
        super.outAct();
        super.setState(state);

        inAct(next);

        if (queue.getCommonQueue() > 0) {
            getFromQueue();
        }
        if(isAllowedToChange()){
            if(changeProcessManagerMap.get(getCurrentId()).TryChangeQueue()){
               IncrementOrCreate(changedById, getCurrentId());
            }
        }
        outStat();
        nextTimeQueue.poll();
    }

    protected void outStat(){
        int currentId = getCurrentId();
        stateById.put(currentId, stateById.get(currentId)-1);
        IncrementOrCreate(completedById, currentId);
    }

    protected void getFromQueue() {
        int queueId = queue.getCurrentQueueId();
        queue.decrementQueue(queueId);
        var timeNext = super.getTimeCurrent() + getDelay(queueId);
        IncrementOrCreate(stateById, queueId);
        setTimeNext(timeNext, queueId);
        var state = getState()+1;
        super.setState(state);
    }

    protected void inAct(Process next){
        if(next != null){
            if(outMap!= null && outMap.containsKey(getCurrentId())){
                next.inAct(outMap.get(getCurrentId()));
            }
            else {
                next.inAct(getCurrentId());
            }
        }
    }
    @Override
    public Element getNextElement() {
        if(super.getNextElement() != null){
            return super.getNextElement();
        }
        if(nextProcessManager == null){
            return null;
        }

        return nextProcessManager.getNextElement(getCurrentId());
    }
    protected boolean isAllowedToChange(){
        return changeProcessManagerMap != null && changeProcessManagerMap.containsKey(getCurrentId());
    }

    public int getFailure() {
       return sumInt(failureById);
    }

    private int sumInt(Map<Integer, Integer> valuesById){
        return valuesById.values().stream().mapToInt(v->v).sum();
    }
    private double sum(Map<Integer, Double> valuesById){
        return valuesById.values().stream().mapToDouble(v->v).sum();
    }



    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("failure = " + this.getFailure());
        System.out.println("queue = " + this.queue.getCommonQueue());
        System.out.println("changed = " + this.getChangeCount());
    }

    @Override
    public void doStatistics(double delta) {
        AddOrCreate(getStateById(), meanLoadById, delta);
        AddOrCreate(queue.currentQueue(), meanQueueById, delta);
    }

    private void AddOrCreate(Map<Integer, Integer> parentMap,Map<Integer, Double> map, double delta){
        for (var item:
             parentMap.entrySet()) {
            if(map.containsKey(item.getKey())){
                map.put(item.getKey(), map.get(item.getKey()) + delta * item.getValue());
            }
            else {
                map.put(item.getKey(), delta* item.getValue());
            }

        }
    }

    protected void IncrementOrCreate(Map<Integer, Integer> map, int id){
        if(map.containsKey(id)){
            map.put(id, map.get(id)+1);
        }
        else {
            map.put(id, 1);
        }
    }
    public double getMeanLoad(){
        return sum(meanLoadById);
    }

    public double getMeanQueue() {
        return sum(meanQueueById);
    }

    public int getMaxState() {
        return maxState;
    }

    public void setMaxState(int maxState) {
        this.maxState = maxState;
    }

    public void setTypeDistribution(Map<Integer, IRandom> queueDistribution) {
        this.typeDistribution = queueDistribution;
    }
    public double getDelay(int id){
        if(typeDistribution != null){
            return typeDistribution.get(id).Random();
        }
        return super.getDelay();
    }
    public boolean isWaitToIN() {
        return waitToIN;
    }

    public void setWaitToIN(boolean waitToIN) {
        this.waitToIN = waitToIN;
    }
    public  boolean canIn(){
        return  getState() != getMaxState();
    }



    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public void setNextProcessManager(NextProcessManager nextProcessManager) {
        this.nextProcessManager = nextProcessManager;
    }
    public int getLast(){
        return queue.getLast();
    }
    public int getCommonQueue(){
        return queue.getCommonQueue();
    }
    public void setOutMap(Map<Integer, Integer> outMap) {
        this.outMap = outMap;
    }
    public void RemoveChanged(int queueId){
        queue.RemoveChanged(queueId);
    }
    public boolean isUseCommonQueue(){
        return queue.isUseCommonQueue();
    }

    public int getCommonMaxQueue() {
        return queue.getCommonMaxQueue();
    }

    public int getMaxQueue(int queueId) {
        return queue.getMaxQueue(queueId);
    }

    public int getQueue(int queueId) {
        return queue.getQueue(queueId);
    }

    public int getChangeCount() {
        return sumInt(changedById);
    }

    public void setChangeProcessManagerMap(Map<Integer, ChangeProcessManager> changeProcessManagerMap) {
        this.changeProcessManagerMap = changeProcessManagerMap;
    }

    public Map<Integer, Integer> getStateById() {
        return stateById;
    }

    public Map<Integer, Double> getMeanQueueById() {
        return meanQueueById;
    }

    public Map<Integer, Double> getMeanLoadById() {
        return meanLoadById;
    }

    public Map<Integer, Integer> getChangedById() {
        return changedById;
    }
    public Map<Integer, Integer> getCompletedById() {return completedById;}

    public int getInCount() {
        return sumInt(inById);
    }
    public Map<Integer, Integer> getInById(){
        return inById;
    }


    @Override
    public void printResult(){
        super.printResult();
        System.out.println("in Quantity =" + getInCount());
    }
}