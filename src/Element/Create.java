package Element;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;

public class Create extends Element {

    private List<Pair<Double, Process>> probabilityProcessList;
    private List<Pair<Double, Integer>> probabilityIdList;
    private List<Process> minProcessChoose;
    private final int defaultId;
    public Create(int defaultId) {
        this.defaultId = defaultId;
    }
    public Create(){
        defaultId = 0;
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTimeNext(super.getTimeCurrent() + super.getDelay());
        getNextElement().inAct(getQueue());
    }

    @Override
    public Process getNextElement() {
        if(minProcessChoose != null){
        var smallest = getSmallestQueue();
        if(smallest != null){
            return smallest;
        }}
        if (probabilityProcessList != null) {
            double probability = Math.random();
            double cumulative = 0.0;
            for (var entry :
                    probabilityProcessList) {

                cumulative += entry.getLeft();
                if (probability <= cumulative) {
                    return entry.getValue();
                }
            }
        }
        return (Process) super.getNextElement();
    }

    public Process getSmallestQueue(){
        return minProcessChoose.stream().min(Comparator.comparing(Process::getCommonQueue))
                .orElse(null);
    }

    public void setMinProcessChoose(List<Process> minProcessChoose) {
        this.minProcessChoose = minProcessChoose;
    }

    public void setProbabilityIdList(List<Pair<Double, Integer>> probabilityIdList) {
        this.probabilityIdList = probabilityIdList;
    }
    public int getQueue(){
        if (probabilityIdList != null) {
            double probability = Math.random();
            double cumulative = 0.0;
            for (var entry :
                    probabilityIdList) {

                cumulative += entry.getLeft();
                if (probability <= cumulative) {
                    return entry.getRight();
                }
            }
        }
        return defaultId;
    }

    public void setProbabilityProcessList(List<Pair<Double, Process>> probabilityProcessList) {
        this.probabilityProcessList = probabilityProcessList;
    }
}


