import Element.Element;

public class Tester {
    public static void main(String[] args) {
        var factor = 4;
        int startRam = 131;
        int startCores = 2;
        int startTime = 10;
        System.out.println("\n-------------TEST-------------");
        System.out.println("time      cores      ram      completed");
        for (int ram = startRam*factor; ram >= startRam; ram/= factor) {
            for (int cores = startCores*factor; cores >= startCores; cores/= factor) {
                for (int meanTime = startTime*factor; meanTime >= startTime; meanTime/= factor) {
                    var completed = ModelCreator.Create(ram, cores, meanTime, false).Completed;
                    System.out.println(meanTime +"         " + cores +"         "+ram+"          "+completed );
                    Element.newModel();
                }
            }
        }

    }
}
