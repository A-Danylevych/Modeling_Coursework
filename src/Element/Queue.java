package Element;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Queue {

    private Map<Integer, Pair<Integer,Integer>> typeQueue;
    private List<Pair<List<Integer>, Deque<Integer>>> queuePriority;
    private final boolean useCommonQueue;
    private int commonMaxQueue;

    public Queue(int maxValue, boolean commonQueue, int firstTypeIndex, int lastTypeIndex, boolean useEqualPriorities){
        useCommonQueue = commonQueue;
        commonMaxQueue = maxValue;
        GenerateQueues(maxValue, firstTypeIndex, lastTypeIndex);
        if (useEqualPriorities){
            GeneratePriories(firstTypeIndex, lastTypeIndex);
        }
    }

    public boolean TryAddToQueue(int queueId){
        if(isUseCommonQueue()){
            if (getCommonQueue() < getCommonMaxQueue()) {
                incrementQueue(queueId);
                return true;
            } else {
                return false;
            }
        }
        else {
            if(getQueue(queueId)<getMaxQueue(queueId)){
                incrementQueue(queueId);
                return true;
            }
            else {
                return false;
            }
        }
    }
    public boolean contains(int id){
        return typeQueue.containsKey(id);
    }
    public void GeneratePriories(int from, int end){
        var equalPriority = new LinkedList<Pair<List<Integer>,Deque<Integer> >>();
        List<Integer> range = IntStream.rangeClosed(from, end)
                .boxed().collect(Collectors.toList());
        equalPriority.add(new ImmutablePair<>(range, new ArrayDeque<>()));
        queuePriority = equalPriority;
    }
    public void GenerateQueues(int queueMax, int from, int end){
        setCommonMaxQueue(queueMax);
        var queueMap = new HashMap<Integer, Pair<Integer, Integer>>();
        for(int i = from; i<= end; i++ ){
            queueMap.put(i, new MutablePair<>(queueMax, 0));
        }
        setTypeQueue(queueMap);
    }

    public int getCommonQueue(){
        int queue = 0;
        for (var item:
                typeQueue.entrySet()) {
            queue += item.getValue().getValue();
        }
        return queue;
    }

    public int getCurrentQueueId(){
        for (var priority:
                queuePriority) {
            if(!priority.getValue().isEmpty()){
                return priority.getValue().peek();
            }
        }
        return -1;
    }

    public void setQueuePriority(List<Pair<List<Integer>, Deque<Integer>>> queuePriority) {
        this.queuePriority = queuePriority;
    }

    public void setTypeQueue(Map<Integer, Pair<Integer, Integer>> typeQueue) {
        this.typeQueue = typeQueue;
    }

    public int getQueue(int queueId) {
        return this.typeQueue.get(queueId).getValue();
    }
    public void setQueue(int queueId, int queue) {
        while (getQueue(queueId)>queue){
            decrementQueue(queueId);
        }
        while (getQueue(queueId)<queue){
            incrementQueue(queueId);
        }
    }


    public int getMaxQueue(int queueId) {
        return  this.typeQueue.get(queueId).getKey();
    }


    public void setMaxQueue(int queueId, int maxQueue, int currentQueue) {
        MutablePair<Integer,Integer> pair = new MutablePair<>(maxQueue, currentQueue);
        this.typeQueue.put(queueId, pair);
    }


    public void incrementQueue(int id){
        typeQueue.get(id).setValue(typeQueue.get(id).getValue()+1);
        for (var priority :
                queuePriority) {
            if (priority.getKey().contains(id)) {
                priority.getValue().add(id);
            }
        }
    }
    public void decrementQueue(int id){
        typeQueue.get(id).setValue(typeQueue.get(id).getValue()-1);
        for (var priority :
                queuePriority) {
            if (priority.getKey().contains(id)) {
                priority.getValue().poll();
            }
        }
    }

    public int getCommonMaxQueue(){
        return this.commonMaxQueue;
    }
    public void setCommonMaxQueue(int maxQueue){
        this.commonMaxQueue = maxQueue;
    }

    public boolean isUseCommonQueue(){
        return this.useCommonQueue;
    }

    public int getLast(){
        for (int i = queuePriority.size() -1 ; i >= 0; i--) {
            var deque = queuePriority.get(i).getRight();
            if (deque != null && !deque.isEmpty()){
                return deque.peekLast();
            }
        }
         return -1;
    }

    public void RemoveChanged(int queueId){
        var pair = typeQueue.get(queueId);
        pair.setValue(pair.getValue()-1);
        typeQueue.put(queueId, pair);
        for (var item :
                queuePriority) {
            if (item.getLeft().contains(queueId)) {
                item.getRight().removeLast();
            }
        }
    }

    public Map<Integer, Integer> currentQueue(){
        var queue = new HashMap<Integer, Integer>();
        for (var item :
                typeQueue.entrySet()) {
            queue.put(item.getKey(), item.getValue().getValue());
        }
        return queue;
    }

}
