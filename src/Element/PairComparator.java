package Element;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;

public class PairComparator implements Comparator<Pair<Double, Integer>> {

    @Override
    public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
        return Double.compare(o1.getLeft(), o2.getLeft());
    }
}
