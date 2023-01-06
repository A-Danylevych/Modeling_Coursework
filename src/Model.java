import Element.Element;
import Element.Process;
import Element.Create;
import Element.ProcessGiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Model {

    private final ArrayList<Element> elements;
    double timeNext, timeCurrent;
    int currentEventId;
    int lastIndex;
    boolean showStat = true;

    public Model(ArrayList<Element> elements) {
        this.elements = elements;
        timeNext = 0.0;
        currentEventId = 0;
        timeCurrent = timeNext;
    }
    public Result simulate(double time) {

        while (timeCurrent < time) {
            timeNext = Double.MAX_VALUE;
            for (Element e : elements) {
                if (e.getTimeNext() < timeNext) {
                    timeNext = e.getTimeNext();
                    currentEventId = e.getId();

                }
            }
            if(showStat){
            System.out.println("\nIt's time for event in " +
                    elements.get(currentEventId).getName() +
                    ", time =   " + timeNext);
            }

            for (Element e : elements) {
                e.doStatistics(timeNext - timeCurrent);
            }
            timeCurrent = timeNext;
            for (Element e : elements) {
                e.setTimeCurrent(timeCurrent);
            }
            elements.get(currentEventId).outAct();
            for (Element e : elements) {
                if (e.getTimeNext() == timeCurrent) {
                    e.outAct();
                }
            }
            if(showStat){
            printInfo();
            }
        }
        return printResult();
    }

    public void printInfo() {
        for (Element e : elements) {
            e.printInfo();
        }
    }
    public Result printResult() {
        if(showStat)
            System.out.println("\n-------------RESULTS-------------");
        int totalCreated = 0;
        int totalFailure = 0;
        double meanTimeInProcess = 0;
        double totalTimeWaiting = 0;
        double totalTimeInModel = 0;
        double ramUsage = 0;
        double count = 0;
        int totalCompleted =0;
        var counted = new LinkedList<Integer>();
        var totalTimeById = new HashMap<Integer, Double>();
        var totalQueueTimeById = new HashMap<Integer, Double>();
        var totalTimeWorkingById = new HashMap<Integer, Double>();
        for (Element e : elements) {
            if(showStat) {
                e.printResult();
            }
            if (e instanceof Create) {
                if (showStat)
                    System.out.println();
            }
            if (e instanceof Process p) {
                if(e.getId() == lastIndex){
                    totalCompleted = e.getQuantity();
                }
                if(showStat) {
                    System.out.println("mean length of queue = " +
                            p.getMeanQueue() / timeCurrent
                            + "\nfailure probability  = " +
                            p.getFailure() / (double) p.getQuantity());
                    System.out.println("mean load of process = " +
                            p.getMeanLoad() / timeCurrent / p.getMaxState());
                    System.out.println("Mean time in process =" +
                            p.getMeanLoad() / p.getQuantity());
                    System.out.println("Mean time waiting =" +
                            p.getMeanQueue() / p.getQuantity());
                    System.out.println("failure =" + p.getFailure());
                    System.out.println("Changes =" + p.getChangeCount());
                }
                totalFailure += p.getFailure();
                double currentTimeInProcess = 0;
                var keys = ((Process) e).getInById().keySet();
                for (var i :
                        keys) {
                    var timeInProcess = p.getMeanLoadById().containsKey(i) ?
                            p.getMeanLoadById().get(i)/p.getInById().get(i):
                            0;
                    var timeWaiting =p.getMeanQueueById().containsKey(i)?
                            p.getMeanQueueById().get(i)/p.getInById().get(i):
                            0;
                    var totalTime =  timeWaiting + timeInProcess;
                    totalTimeInModel += totalTime;
                    currentTimeInProcess += timeInProcess;
                    totalTimeWaiting += timeWaiting;
                    if(!counted.contains(i)){
                        count++;
                        counted.add(i);
                    }

                    AddOrCreate(totalQueueTimeById, i, timeWaiting);
                    AddOrCreate(totalTimeById,i, totalTime);
                    AddOrCreate(totalTimeWorkingById, i, timeInProcess);

                    var load = p.getMeanLoadById().containsKey(i) ?
                            p.getMeanLoadById().get(i) / timeCurrent / p.getMaxState() :
                            0;

                    if(showStat) {
                        System.out.println("Type " + i + " mean load of process = " + load);
                        System.out.println("Type " + i + " mean total time in process =" +
                                totalTime);
                        System.out.println("Type " + i + " mean time waiting =" +
                                timeWaiting);
                        System.out.println("Type " + i + " mean time in process =" +
                                timeInProcess);
                        System.out.println();
                    }
                }
                var completed = p.getCompletedById().keySet().size();
                meanTimeInProcess += currentTimeInProcess/completed;
            }
            if(e instanceof ProcessGiver pg){
                ramUsage = pg.getRamUsage();
            }
        }
        if(showStat) {
            System.out.println("failure probability=" + (double) totalFailure / totalCreated);
            for (var item :
                    totalTimeById.entrySet()) {
                System.out.println("Type " + item.getKey() + " mean time=" + item.getValue());
            }
            for (var item :
                    totalQueueTimeById.entrySet()) {
                System.out.println("Type " + item.getKey() + " mean waiting time=" + item.getValue());
            }
            for (var item :
                    totalTimeWorkingById.entrySet()) {
                System.out.println("Type " + item.getKey() + " mean in process time=" + item.getValue());
            }

            System.out.println("All types mean in model time=" + totalTimeInModel / count);
            System.out.println("All types mean in process time=" + meanTimeInProcess);
            System.out.println("All types mean waiting time=" + totalTimeWaiting / count);
            System.out.println("Mean RAM usage=" + ramUsage / timeCurrent);
        }
        var result = new Result();
        result.Completed = totalCompleted;
        return result;
    }
    private void AddOrCreate(Map<Integer, Double> map, int id, double value){
        if(map.containsKey(id)){
            map.put(id, map.get(id) + value);
        }
        else {
            map.put(id, value);
        }
    }
    public void setLastIndex(int lastIndex){
        this.lastIndex = lastIndex;
    }
    public void setShowStat(boolean showStat){
        this.showStat = showStat;
    }
}

