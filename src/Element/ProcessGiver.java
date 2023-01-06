package Element;

import java.util.Map;

public class ProcessGiver extends Process{
    private final Locker locker;
    private Map<Integer, Integer> idGive;
    private double ramUsage;

    public ProcessGiver(Locker locker) {
        super();
        this.locker = locker;
    }

    public void take() {
    }

    @Override
    protected void outStat() {
        super.outStat();
        locker.give(idGive.get(getCurrentId()));
    }

    public void setIdGive(Map<Integer, Integer> idGive) {
        this.idGive = idGive;
    }

    @Override
    public void doStatistics(double delta) {
        super.doStatistics(delta);
        ramUsage = ramUsage + locker.getUsed()*delta;
    }

    public double getRamUsage() {
        return ramUsage/locker.getMaxAvailable();
    }
}
