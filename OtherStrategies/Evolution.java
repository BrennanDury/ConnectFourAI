package connectFour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Evolution {

    public Player[] evolvePlayers(PlayerFactory factory, int n_candidates, int n_iter) {
        Player[] candidates = initializeCandidates(factory, n_candidates);
        for (int iter = 0; iter < n_iter; iter++) {
            nextGeneration(factory, candidates);
            System.out.println("Iteration " + (iter + 1) + "/" + n_iter + " complete");
        }
        return candidates;
    }

    private Player[] initializeCandidates(PlayerFactory factory, int n_candidates) {
        Player[] candidates = new Player[n_candidates];
        int geneticLength = factory.geneticLength();
        for (int i = 0; i < n_candidates; i++) {
            Object[] genetics = new Object[geneticLength];
            for (int position = 0; position < geneticLength; position++) {
                genetics[position] = factory.initializeParameter(position);
            }
            Player player = factory.createPlayer(genetics);
            candidates[i] = player;
        }
        return candidates;
    }

    private void nextGeneration(PlayerFactory factory, Player[] candidates) {
        Collections.shuffle(Arrays.asList(candidates));

        Object[] prevWinningGenetics = candidates[0].getGenetics();

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < candidates.length - 1; i += 2) {
            indices.add(i);
        }

        List<Integer> result = indices.parallelStream()
              .map(i -> {
        return Runner.repeatedGame(candidates[i], candidates[i + 1]);})
        .collect(Collectors.toList());

        for (int i = 0; i < result.size(); i++) {
            evolve(i, result.get(i), candidates, factory, prevWinningGenetics);
        }
    }

    private void evolve(int i, int result, Player[] candidates, PlayerFactory factory, Object[] prevWinningGenetics) {
        int winningIndex = i + result - 1;
        Player winner = candidates[winningIndex];
        Object[] winningGenetics = winner.getGenetics();
        Object[] mutations = new Object[winningGenetics.length];
        for (int position = 0; position < winningGenetics.length; position++) {
            if (i % 4 < 2) {
                mutations[position] = mutate(factory, (float) winningGenetics[position], position);
            } else {
                if (Math.random() > 0.5) {
                    mutations[position] = mutate(factory, (float) winningGenetics[position], position);
                } else {
                    mutations[position] = mutate(factory, (float) prevWinningGenetics[position], position);
                }
            }
        }
        Player child = factory.createPlayer(mutations);
        for (int j = 0; j < winningGenetics.length; j++) {
            prevWinningGenetics[j] = winningGenetics[j];
        }
        candidates[i] = winner;
        candidates[i + 1] = child;
    }

    private float mutate(PlayerFactory factory, float val, int position) {
        boolean mutates = Math.random() > 0.15;
        if (mutates) {
            val = factory.mutateParameter(val, position);
        }
        return val;
    }

    private final class Tuple {
        private final Player player1;
        private final Player player2;

        public Tuple(Player player1, Player player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        public Player getPlayer1() {
            return this.player1;
        }

        public Player getPlayer2() {
            return player2;
        }
    }
}

