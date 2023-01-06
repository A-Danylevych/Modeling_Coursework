import Element.Create;
import Element.Element;
import Element.Process;
import Element.Queue;
import Random.UniformRandom;
import Random.PoissonRandom;
import Random.NormRandom;
import Random.ExpRandom;
import Random.ChannelRandom;
import Element.Locker;
import Element.ProcessTaker;
import Element.ProcessGiver;
import Element.NextProcessManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class ModelCreator {

    public static Result Create(int ramCount, int cores, double meanTime, boolean showStat) {
        // Створення СМО
        Create task = new Create();
        Create interruption = new Create();
        Locker ram = new Locker(ramCount, ramCount);
        ProcessTaker processor = new ProcessTaker(ram);
        Process disk1 = new Process();
        Process disk2 = new Process();
        Process disk3 = new Process();
        Process disk4 = new Process();
        ProcessGiver channel = new ProcessGiver(ram);
        // Налаштування створення рівномірних завдань для кожного диску та розміру
        task.setDistribution(new PoissonRandom(5));

        double probability = 1./((60-20 +1)*4);
        var probabilityQueueList = new ArrayList<Pair<Double, Integer>>();
        for (int i = 1; i <= 4; i++) {
            for (int j = 20; j <= 60; j++) {
                probabilityQueueList.add(new ImmutablePair<>(probability, i*100+j));
            }
        }
        task.setProbabilityIdList(probabilityQueueList);
        task.setNextElement(processor);
        // Налаштування переривань
        interruption.setDistribution(new UniformRandom(2, 10));
        interruption.setNextElement(processor);

        // Налаштування СМО процесор
        processor.setDistribution(new NormRandom(meanTime, 3));
        processor.setInterruptDelay(new ExpRandom(1./6));
        processor.setMaxState(cores);
        processor.setInterrupter(interruption);



        // Пріорітети черги процесора та кількість займаної опертивної пам'яті для кожного замовлення
        var processorQueue = new Queue(Integer.MAX_VALUE, true,
                0, 460, false);
        var priorityList =  new LinkedList<Pair<List<Integer>,Deque<Integer> >>();
        var idTake = new HashMap<Integer, Integer>();
        idTake.put(0,0);
        priorityList.add(new ImmutablePair<>(List.of(0), new ArrayDeque<>()));
        for (int i = 20; i <= 60; i++) {
            var list = List.of(100 + i, 200+i, 300+i, 400+i);
            for (var key :
                    list) {
                idTake.put(key, i);
            }
            priorityList.add(new ImmutablePair<>(list, new ArrayDeque<>()));
        }
        processorQueue.setQueuePriority(priorityList);
        processor.setQueue(processorQueue);
        processor.setIdTake(idTake);

        // Вибір диска в залежності від ід замовлення та налаштування розподілу для дисків
        var processorToDisk = new NextProcessManager();
        
        var idDisk = new HashMap<Integer, Process>();
        idDisk.put(0, null);
        var distList = List.of(disk1, disk2, disk3, disk4);
        var uniformDiskRandom = new UniformRandom(0.0, 0.075);
        var index = 100;
        for (var disk :
                distList) {
            for (int i = 20; i <= 60; i++) {
                idDisk.put(index +i, disk);
                disk.setDistribution(uniformDiskRandom);
                disk.setNextElement(channel);
                disk.setInfiniteQueue(0, 460);
            }
            index += 100;
        }
        processorToDisk.setIdToProcess(idDisk);
        processor.setNextProcessManager(processorToDisk);

        // Налаштування СМО Канал обслуговування
        channel.setInfiniteQueue(0,460);
        channel.setIdGive(idTake);
        channel.setDistribution(new ChannelRandom());
        ram.setGiver(channel);
        ram.setTaker(processor);

        var list = new ArrayList<Element>();
        list.add(task);
        task.setName("Task");
        list.add(interruption);
        interruption.setName("Interruption");
        list.add(processor);
        processor.setName("Processor");
        list.add(disk1);
        disk1.setName("Disk 1");
        list.add(disk2);
        disk2.setName("Disk 2");
        list.add(disk3);
        disk3.setName("Disk 3");
        list.add(disk4);
        disk4.setName("Disk 4");
        list.add(channel);
        channel.setName("Channel");


        Model model = new Model(list);
        model.setLastIndex(channel.getId());
        model.setShowStat(showStat);
        return model.simulate(6000.0);
    }
}