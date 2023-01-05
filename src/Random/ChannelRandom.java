package Random;


public class ChannelRandom implements IRandom {
    private final UniformRandom uniformRandom;
    public ChannelRandom(){

        uniformRandom = new UniformRandom(0, 25);
    }
    @Override
    public double Random() {
        return 0.001*(2.5 + uniformRandom.Random());
    }
}
