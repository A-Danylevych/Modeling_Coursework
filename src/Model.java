import Element.Element;
import Element.Process;
import Element.Create;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Model {

    private final ArrayList<Element> elements;
    double timeNext, timeCurrent;
    int currentEventId;

    public Model(ArrayList<Element> elements) {
        this.elements = elements;
        timeNext = 0.0;
        currentEventId = 0;
        timeCurrent = timeNext;
    }
    public void simulate(double time) {

        while (timeCurrent < time) {
            timeNext = Double.MAX_VALUE;
            for (Element e : elements) {
                if (e.getTimeNext() < timeNext) {
                    timeNext = e.getTimeNext();
                    currentEventId = e.getId();

                }
            }
            System.out.println("\nIt's time for event in " +
                    elements.get(currentEventId).getName() +
                    ", time =   " + timeNext);
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
            printInfo();
        }
        printResult();
    }

    public void printInfo() {
        for (Element e : elements) {
            e.printInfo();
        }
    }
    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        int totalCreated = 0;
        int totalFailure = 0;
        var totalTimeById = new HashMap<Integer, Double>();
        var totalQueueTimeById = new HashMap<Integer, Double>();
        var totalTimeWorkingById = new HashMap<Integer, Double>();
        for (Element e : elements) {
            e.printResult();
            if (e instanceof Create){
                totalCreated = e.getQuantity();
            }
            if (e instanceof Process p) {
                System.out.println("mean length of queue = " +
                        p.getMeanQueue() / timeCurrent
                        + "\nfailure probability  = " +
                        p.getFailure() / (double) p.getQuantity());
                System.out.println("mean load of process = " +
                        p.getMeanLoad() / timeCurrent);
                System.out.println("Mean time in process =" +
                        p.getMeanLoad()/p.getQuantity());
                System.out.println("Mean time waiting ="+
                        p.getMeanQueue()/p.getQuantity());
                System.out.println("failure =" + p.getFailure());
                totalFailure += p.getFailure();
                System.out.println("Changes =" + p.getChangeCount());
                var keys = ((Process) e).getStateById().keySet();
                for (var i :
                        keys) {
                    var timeInProcess = p.getMeanLoadById().get(i)/p.getCompletedById().get(i);
                    var timeWaiting = p.getMeanQueueById().get(i)/p.getCompletedById().get(i);
                    var totalTime =  timeWaiting + timeInProcess;
                    AddOrCreate(totalQueueTimeById, i, timeWaiting);
                    AddOrCreate(totalTimeById,i, totalTime);
                    AddOrCreate(totalTimeWorkingById, i, timeInProcess);
                }
            }
        }
        System.out.println("failure probability=" + (double)totalFailure/totalCreated);
        for (var item :
                totalTimeById.entrySet()) {
            System.out.println("Type " + item.getKey() + " mean time=" + item.getValue() );
        }
        for (var item :
                totalQueueTimeById.entrySet()) {
            System.out.println("Type " + item.getKey() + " mean waiting time=" + item.getValue() );
        }
        for (var item :
                totalTimeWorkingById.entrySet()) {
            System.out.println("Type " + item.getKey() + " mean in process time=" + item.getValue() );
        }
    }
    private void AddOrCreate(Map<Integer, Double> map, int id, double value){
        if(map.containsKey(id)){
            map.put(id, map.get(id) + value);
        }
        else {
            map.put(id, value);
        }
    }
}

