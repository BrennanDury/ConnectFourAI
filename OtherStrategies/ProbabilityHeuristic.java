package connectFour;

import com.google.gson.Gson;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProbabilityHeuristic extends Minimax {
    private static final int N_SIMULATIONS = 1000;
    //public static MutableLongLongMap boardsSeen = new LongLongHashMap().asSynchronized();
    //public static ConcurrentSkipListMap<Long, Long> boardsSeen = new ConcurrentSkipListMap<>();

    protected ProbabilityHeuristic(int maxdepth, Object[] vector) {
        super(maxdepth, vector);
        //    boardsSeen = LongLongHashMap.class).asSynchronized();
    }

    @Override
    protected float getHeuristicScore(Grid g, int myPlayer) {
        List<Short> results = new LinkedList<>();
        for (int i = 0; i < N_SIMULATIONS; i++) {
            try {
                results.add(new Simulation(g.makeCopy(), boardsSeen).call());
            } catch (Exception e) {
                System.exit(10);
            }
        }
        int p1 = 0;
        int p2 = 0;
        for (int result : results) {
            if (result == 1) {
                p1++;
            } else if (result == 2) {
                p2++;
            }
        }
        int wins;
        int losses;
        if (myPlayer == Grid.PLAYER1) {
            wins = p1;
            losses = p2;
        } else {
            wins = p2;
            losses = p1;
        }
        long boardState = g.getBoardState();
        float score = (float) (wins) / (wins + losses);
        this.boardsSeen.put(boardState, score);
        return score;
    }

    @Override
    public Object[] getGenetics() {
        return new Object[]{boardsSeen};
    }
}
