import Element.Create;
import Element.Element;
import Element.Process;
import Element.Queue;
import Element.ChangeProcessManager;
import Random.ExpRandom;
import Random.NormRandom;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Create c = new Create(0);
        Process p1 = new Process();
        Process p2 = new Process();
        c.setDistribution(new ExpRandom(0.5));
        p1.setDistribution(new NormRandom(1.0, 0.3));
        p2.setDistribution(new NormRandom(1.0, 0.3));

        System.out.println("id0 = " + c.getId() + "   id1=" + p1.getId() + "   id2 = " + p2.getId());
        List<Process> uniformChoose = new ArrayList<>();
        uniformChoose.add(p1);
        uniformChoose.add(p2);
        c.setMinProcessChoose(uniformChoose);
        c.setTimeNext(0.1);

        p1.setMaxState(1);
        p2.setMaxState(1);

        var queue1 = new Queue(3, true,0,0, true);
        queue1.setQueue(0, 2);
        var queue2 = new Queue(3, true,0,0, true);
        queue2.setQueue(0, 2);
        p1.setQueue(queue1);
        p2.setQueue(queue2);
        p1.inAct(0);
        p2.inAct(0);

        var nextManager1 = new ChangeProcessManager(p1);
        var nextManager2 = new ChangeProcessManager(p2);

        nextManager1.setChangeProcess(List.of(p2));
        nextManager2.setChangeProcess(List.of(p1));
        nextManager1.setDifferenceToChange(2);
        nextManager2.setDifferenceToChange(2);
        p1.setChangeProcessManagerMap(Map.of(0, nextManager1));
        p2.setChangeProcessManagerMap(Map.of(0, nextManager2));

        c.setName("CREATOR");
        p1.setName("Queue 1");
        p2.setName("Queue 2");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);

        Model model = new Model(list);
        model.simulate(800.0);
    }
}