package Element;

import Random.UniformRandom;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;


public class NextProcessManager {
    private Map<Integer, List<Pair<Double, Process>>> probabilityProcessMap;
    private Map<Integer, Process> queueProcessMap;
    public NextProcessManager(){

    }
    public Element getNextElement(int currentId) {
        if(probabilityProcessMap != null && probabilityProcessMap.containsKey(currentId)) {
           UniformRandom random = new UniformRandom(0.0, 1.0);
           var probabilityProcessList = probabilityProcessMap.get(currentId);
           double probability = random.Random();
           double cumulativeProbability = 0.0;
           for (var entry:
                   probabilityProcessList) {

               cumulativeProbability += entry.getLeft();
               if (probability <= cumulativeProbability) {
                   return entry.getRight();
               }
           }
       }
        else if(queueProcessMap != null) {
            return queueProcessMap.get(currentId);
        }
        return null;
    }

    public void setIdToProcess(Map<Integer, Process> queueProcessMap) {
        this.queueProcessMap = queueProcessMap;
    }

    public void setProbabilityToProcess(Map<Integer, List<Pair<Double, Process>>> probabilityProcessList){
        this.probabilityProcessMap = probabilityProcessList;
    }


}
