package Element;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ChangeProcessManager {
    private final Process p;
    private List<Process> changeProcess;
    private int differenceToChange;
    private int queueToChange;

    public ChangeProcessManager(Process myProcess) {
        this.p = myProcess;
    }
    public void setDifferenceToChange(int differenceToChange) {
        this.differenceToChange = differenceToChange;
    }

    public void setChangeProcess(List<Process> changeProcess) {
        this.changeProcess = changeProcess;
    }
    public Pair<Process, Integer> canChangeProcess(){
        for (var item :
                changeProcess) {
            var difference = p.isUseCommonQueue() && item.isUseCommonQueue()
                    ? item.getCommonQueue() - p.getCommonQueue():
                    item.getQueue(queueToChange) - p.getQueue(queueToChange);
            if (difference<0){
                continue;
            }
            if (differenceToChange<=difference){
                return new ImmutablePair<>(item, item.getLast());
            }
        }
        return new ImmutablePair<>(null, 0);
    }

    public boolean TryChangeQueue(){
        if (p.isUseCommonQueue() && p.getCommonMaxQueue() == p.getCommonQueue()){
            return false;
        }else if (!p.isUseCommonQueue() && p.getMaxQueue(queueToChange) == p.getQueue(queueToChange))
        {
            return false;
        }
        else {
            var pair = canChangeProcess();
            if (pair.getLeft() != null) {
                p.inAct(queueToChange);
                pair.getLeft().RemoveChanged(pair.getRight());
                return true;
            }
            return false;
        }
    }
    public void setQueueToChange(int queueToChange) {
        this.queueToChange = queueToChange;
    }
}
