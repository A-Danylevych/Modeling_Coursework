package Element;

import Random.IRandom;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcessTaker extends Process{

    private final Locker locker;
    private double lockUsage;
    private double interruptTime;
    private IRandom interruptDelay;
    private Create interrupter;

    private int lockerId;
    private Map<Integer, Integer> idTake;

    public ProcessTaker(Locker locker) {
        super();
        this.locker = locker;
    }

    @Override
    public void doStatistics(double delta) {
        super.doStatistics(delta);
        lockUsage = lockUsage + delta * locker.getUsage();
    }

    public void give(){
        if(queue.getCommonQueue() > 0 && super.maxState > super.getState()){
            getFromQueue();
        }
    }

    @Override
    public void inAct(int queueId) {
        if(queueId == lockerId){
            if(!nextTimeQueue.isEmpty()){
                var pair = nextTimeQueue.poll();
                nextTimeQueue.add(new ImmutablePair<>(pair.getLeft()+interruptDelay.Random(),pair.getRight()));
            }
            else {
                interruptTime = getTimeCurrent() + interruptDelay.Random();
            }
            if(getState() != 0){
                Interrupt();
            }
        }else {
            setInCount(getInCount()+1);
        if (super.getState() < maxState && locker.canTake(idTake.get(queueId))) {
            double timeNext;
            if(getState() == getMaxState() && interruptTime > getTimeCurrent()){
                timeNext  = super.getTimeCurrent() + getDelay(queueId) + interruptTime-getTimeCurrent();
            }
            else {
                timeNext  = super.getTimeCurrent() + getDelay(queueId);
            }
            locker.take(idTake.get(queueId));

            IncrementOrCreate(stateById, queueId);
            super.setState(getState()+1);
            setTimeNext(timeNext, queueId);
        } else {
            if(!queue.TryAddToQueue(queueId)){
                IncrementOrCreate(failureById, queueId);
            }
        }
        }
    }
    public void Interrupt() {
        super.outAct();
    }
    @Override
    protected void getFromQueue() {
        if(getState() == getMaxState()){
            return;
        }

        var queueId = queue.getCurrentQueueId();
        if (locker.canTake(idTake.get(queueId))) {
            locker.take(idTake.get(queueId));
            queue.decrementQueue(queueId);
            var timeNext = super.getTimeCurrent() + getDelay(queueId);
            IncrementOrCreate(stateById, queueId);
            setTimeNext(timeNext, queueId);
            var state = getState() + 1;
            super.setState(state);
        }
    }

    @Override
    public void outAct() {
        setTimeNext(interrupter.getTimeNext(), getCurrentId());
        nextTimeQueue.poll();
    }

    public void setIdTake(Map<Integer, Integer> idTake) {
        this.idTake = idTake;
    }

    public void setLockerId(int lockerId) {
        this.lockerId = lockerId;
    }

    public void setInterruptDelay(IRandom interruptDelay) {
        this.interruptDelay = interruptDelay;
    }

    public void setInterrupter(Create interrupter) {
        this.interrupter = interrupter;
    }
}
