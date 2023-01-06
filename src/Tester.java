import Element.Element;

public class Tester {
    public static void main(String[] args) {
        var factor = 4;
        int startRam = 131;
        int startCores = 2;
        int startTime = 10;
        System.out.println("\n-------------TEST-------------");
        System.out.println("ram      cores      time      completed");
        for (int ram = startRam; ram <= startRam* factor; ram*= factor) {
            for (int cores = startCores; cores <= startCores*factor; cores*= factor) {
                for (int meanTime = startTime; meanTime <= startTime*factor; meanTime*= factor) {
                    var completed = ModelCreator.Create(ram, cores, meanTime, false).Completed;
                    System.out.println(ram +"         " + cores +"         "+meanTime+"          "+completed );
                    Element.newModel();
                }
            }
        }

    }
}
