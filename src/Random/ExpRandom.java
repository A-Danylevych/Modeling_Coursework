package Random;

public class ExpRandom implements IRandom{
    private final double timeMean;

    public ExpRandom(double timeMean) {
        this.timeMean = timeMean;
    }

    @Override
    public double Random() {
        double a = 0;
        while (a == 0) {
            a = Math.random();
        }
        a = -timeMean * Math.log(a);

        return a;
    }
}
