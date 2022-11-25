package Random;

import java.util.Random;

public class NormRandom implements IRandom{
    private final double timeMean;
    private final double timeDeviation;

    public NormRandom(double timeMean, double timeDeviation) {
        this.timeMean = timeMean;
        this.timeDeviation = timeDeviation;
    }

    @Override
    public double Random() {
        double a;
        Random r = new Random();
        a = timeMean + timeDeviation * r.nextGaussian();

        return a;
    }
}
