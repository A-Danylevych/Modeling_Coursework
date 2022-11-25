import Element.Create;
import Element.Element;
import Element.Process;
import Element.Queue;
import Element.NextProcessManager;
import Random.ExpRandom;
import Random.UniformRandom;
import Random.ErlangRandom;
import Random.IRandom;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Main2 {
    public static void main(String[] args) {
        // Створення процесів та їх розподілів
        Create c = new Create();
        Process reception = new Process();
        Process goToRoom = new Process();
        Process goToLab = new Process();
        Process labReception = new Process();
        Process lab = new Process();
        Process goToReception = new Process();
        c.setDistribution(new ExpRandom(15));
        labReception.setDistribution(new ErlangRandom(4.5, 3));
        lab.setDistribution(new ErlangRandom(4, 2));
        goToRoom.setDistribution(new UniformRandom(3,8));
        goToLab.setDistribution(new UniformRandom(2,  5));
        goToReception.setDistribution(new UniformRandom(2,5));
        // Розподіли для різних типів хворих
        var TypeDistribution = new HashMap<Integer, IRandom>();
        TypeDistribution.put(1, new ExpRandom(15));
        TypeDistribution.put(2, new ExpRandom(40));
        TypeDistribution.put(3, new ExpRandom(30));
        reception.setQueueDistribution(TypeDistribution);

        // Різна вірогідність генерації типів хворих
        var probabilityQueueList = new ArrayList<Pair<Double, Integer>>();
        probabilityQueueList.add(new ImmutablePair<>(0.5, 1));
        probabilityQueueList.add(new ImmutablePair<>(0.1, 2));
        probabilityQueueList.add(new ImmutablePair<>(0.4, 3));
        c.setProbabilityIdList(probabilityQueueList);
        c.setNextElement(reception);

        // Порядок обслуговування хворих
        var receptionQueue = new Queue(Integer.MAX_VALUE, true, 1, 3,
                false);
        var receptionPriority = new LinkedList<Pair<List<Integer>,Deque<Integer> >>();
        receptionPriority.add(new ImmutablePair<>(List.of(1), new ArrayDeque<>()));
        receptionPriority.add(new ImmutablePair<>(List.of(2,3), new ArrayDeque<>()));
        receptionQueue.setQueuePriority(receptionPriority);

        reception.setQueue(receptionQueue);


        // Наступні процеси для хворих
        var receptionNext = new NextProcessManager();
        var queueProcess = new HashMap<Integer, Process>();
        queueProcess.put(1, goToRoom);
        queueProcess.put(2, goToLab);
        queueProcess.put(3, goToLab);
        receptionNext.setIdToProcess(queueProcess);
        reception.setNextProcessManager(receptionNext);

        //Кількість лікарів
        reception.setMaxState(2);

        // Єдина черга, якої нема
        var goToRoomQueue = new Queue(0,true, 1, 1, true);
        goToRoom.setQueue(goToRoomQueue);
        // Кількість супроводжуючих
        goToRoom.setMaxState(3);
        // Чекати супроводжуючого
        goToRoom.setWaitToIN(true);

        var goToLabQueue = new Queue(0 , true, 2,3,true);
        goToLab.setQueue(goToLabQueue);
        goToLab.setMaxState(Integer.MAX_VALUE);

        goToLab.setNextElement(labReception);
        var labReceptionQueue = new Queue(Integer.MAX_VALUE, true, 2,3,
                true);
        labReception.setQueue(labReceptionQueue);
        labReception.setNextElement(lab);

        var labQueue = new Queue(Integer.MAX_VALUE, true, 2, 3, true);
        lab.setQueue(labQueue);
        lab.setMaxState(2);

        var queueProcessLab = new HashMap<Integer, Process>();
        queueProcessLab.put(2, null);
        queueProcessLab.put(3, goToReception);
        var labNext = new NextProcessManager();
        labNext.setIdToProcess(queueProcessLab);
        lab.setNextProcessManager(labNext);

        goToReception.setMaxState(Integer.MAX_VALUE);
        var goToReceptionQueue = new Queue(0, true, 3,3, true);
        goToReception.setQueue(goToReceptionQueue);

        goToReception.setNextElement(reception);
        var outMap = new HashMap<Integer, Integer>();
        outMap.put(3, 1);
        goToReception.setOutMap(outMap);

        c.setName("Create");
        labReception.setName("LabReception");
        lab.setName("lab");
        reception.setName("reception");
        goToRoom.setName("goToRoom");
        goToLab.setName("goToLab");
        goToReception.setName("goToReception");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(reception);
        list.add(goToRoom);
        list.add(goToLab);
        list.add(labReception);
        list.add(lab);
        list.add(goToReception);

        for (var item :
                list) {
            item.setTimeNext(Double.MAX_VALUE);
        }

        c.setTimeNext(0.0);

        Model model = new Model(list);
        model.simulate(10000.0);
    }
}
