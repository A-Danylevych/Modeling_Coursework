package Random;

public class UniformRandom implements IRandom{
    private final double min;
    private final double max;

    public UniformRandom(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public double Random() {
        double a = 0;
        while (a == 0) {
            a = Math.random();
        }
        a = min + a * (max - min);

        return a;
    }
}
