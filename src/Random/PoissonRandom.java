package Random;

public class PoissonRandom implements IRandom{
    private final double timeMean;

    public PoissonRandom(double timeMean) {
        this.timeMean = timeMean;
    }

    @Override
    public double Random() {
        double L = Math.exp(-timeMean);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        } while (p > L);

        return k - 1;
    }
}
