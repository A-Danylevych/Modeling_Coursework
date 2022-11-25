package Random;

public class ErlangRandom implements IRandom {
    private final ExpRandom random;
    private final int k;

    public ErlangRandom(double mean, int k) {
        this.random = new ExpRandom(mean);
        this.k = k;
    }

    @Override
    public double Random() {
        double value = 0.0;
        for (int i =0; i < k; i++){
            value += random.Random();
        }
        return value/k;
    }
}
