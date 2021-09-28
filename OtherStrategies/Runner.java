package connectFour;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Runner {
    public static void main(String[] args) throws IOException, InterruptedException {
        interact("");
        //simulateEvolution();
        //trainProbability();
        //generateData();
    }

    public static int playGame(Player player1, Player player2) {
        int winningPlayer = 0;
        Board board = new Board();
        while (winningPlayer == 0) {
            int p1move = player1.getMoveColumn(board);
            board.makeMove(p1move);
            winningPlayer = board.getWinningPlayer();
            if (winningPlayer == 0) {
                int p2move = player2.getMoveColumn(board);
                board.makeMove(p2move);
                winningPlayer = board.getWinningPlayer();
            }
        }
        return winningPlayer;
    }

    public static void interact(String fileName) throws IOException, InterruptedException {
        PlayerFactory factory = new ProbabilityHeuristicPlayerFactory();
        Object[] parameters = new Object[]{};
        /*if (!fileName.equals("")) {
            Gson gson = new Gson();
            parameters = (Object[]) gson.fromJson(Files.readString(Path.of(fileName)), Float[].class);
            parameters = new Object[]{Files.readString(Path.of(fileName))};
        }*/
        //for (int i = 0; i < parameters.length; i++) {
            //parameters[i] = factory.initializeParameter(i);
        //}
        //Object[] parameters = new Object[]{};
        //PlayerFactory factory = new ProbabilityHeuristicPlayerFactory();
        Player ai = factory.createPlayer(parameters);

        //PlayerFactory factory2 = new PerceptronHeuristicPlayerFactory();
        //Object[] parameters2 = new Object[factory.geneticLength()];
        //for (int i = 0; i < parameters2.length; i++) {
        //    parameters2[i] = factory2.initializeParameter(i);
        //}
        //Player human = factory2.createPlayer(parameters);
        Player human = new InteractivePlayer();

        Player player1;
        Player player2;
        if (Math.random() > 0.5) {
            player1 = human;
            player2 = ai;
        } else {
            player1 = ai;
            player2 = human;
        }
        int winningPlayer = 0;
        GamePanel gamePanel = new GamePanel();
        Board board = new Board();
        while (winningPlayer == 0) {
            gamePanel.drawGrid(board);
            int p1move = player1.getMoveColumn(board);
            board.makeMove(p1move);
            gamePanel.drawGrid(board);
            winningPlayer = board.getWinningPlayer();
            if (winningPlayer == 0) {
                int p2move = player2.getMoveColumn(board);
                board.makeMove(p2move);
                gamePanel.drawGrid(board);
                winningPlayer = board.getWinningPlayer();
            }
        }
        if (winningPlayer == -1) {
            System.out.println("Game over. It was a draw.");
            return;
        }
        if (factory.geneticLength() == 0 && ((player1 == ai && winningPlayer == 1)
                || (player2 == ai && winningPlayer == 2))) {
            System.out.println("Game over. I win. So... tell me more about paperclips.");
        } else {
            String winningPlayerName =
                    (winningPlayer == 1) ? player1.getPlayerName() : player2.getPlayerName();
            System.out.print("Game over.  Player " + winningPlayer + ": " + winningPlayerName + " won.");
        }
    }

    public static int repeatedGame(Player player1, Player player2) {
        int[] wins = new int[]{0, 0};
        for (int i = 0; i < 1; i++) {
            int winner = playGame(player1, player2);
            System.out.println(winner);
            if (winner != -1) {
                wins[winner - 1] += 1;
            }
            int reverseWinner = playGame(player2, player1);
            if (reverseWinner != -1) {
                wins[reverseWinner % 2] += 1;
                System.out.println(3 - reverseWinner);
            } else {
                System.out.println(-1);
            }
        }
        if (wins[0] > wins[1]) {
            return 1;
        } else {
            return 2;
        }
    }

    public static void simulateEvolution() throws IOException {
        Evolution simulation = new Evolution();
        Player[] players = simulation.evolvePlayers(new PerceptronHeuristicPlayerFactory(), 16, 1000000);

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < players.length - 1; i += 2) {
            indices.add(i);
        }

        List<Player> winners = new ArrayList<Player>();
        for (Player player : players) {
            winners.add(player);
        }

        List<Player> playersList0 = Arrays.asList(players);
        List<Player> newPlayersList = indices.parallelStream().map(x -> {
            return compete(x, playersList0, winners);
        }).collect(Collectors.toList());

        List<Player> playersList1 = newPlayersList;
        indices = new ArrayList<>();
        for (int i = 0; i < playersList1.size() - 1; i += 2) {
            indices.add(i);
        }
        newPlayersList = indices.parallelStream().map(x -> {
            return compete(x, playersList1, winners);
        }).collect(Collectors.toList());
        List<Player> playersList2 = newPlayersList;
        indices = new ArrayList<>();
        for (int i = 0; i < playersList2.size() - 1; i += 2) {
            indices.add(i);
        }
        newPlayersList = indices.parallelStream().map(x -> {
            return compete(x, playersList2, winners);
        }).collect(Collectors.toList());
        List<Player> playersList3 = newPlayersList;
        indices = new ArrayList<>();
        for (int i = 0; i < playersList3.size() - 1; i += 2) {
            indices.add(i);
        }
        newPlayersList = indices.parallelStream().map(x -> {
            return compete(x, playersList3, winners);
        }).collect(Collectors.toList());
        List<Player> playersList4 = newPlayersList;
        indices = new ArrayList<>();
        for (int i = 0; i < playersList4.size() - 1; i += 2) {
            indices.add(i);
        }
        newPlayersList = indices.parallelStream().map(x -> {
            return compete(x, playersList4, winners);
        }).collect(Collectors.toList());

        Player best = winners.get(0);

        FileWriter file = new FileWriter(new Date().toString() + ".txt");
        file.write(best.getPlayerName());
        file.flush();
        file.close();
    }

    private static Player compete(int i, List<Player> players, List<Player> winners) {
        Player player1 = players.get(i);
        Player player2 = players.get(i + 1);
        int result = Runner.repeatedGame(player1, player2);
        Player loser;
        Player winner;
        if (result == 1) {
            loser = player2;
            winner = player1;
        } else {
            loser = player1;
            winner = player2;
        }
        winners.remove(loser);
        return winner;
    }
    
    public static void trainProbability() throws IOException {
        PlayerFactory factory = new ProbabilityHeuristicPlayerFactory();
        Player player = factory.createPlayer(new Object[0]);
        for (int i = 0; i < 1; i++) {
            player.getMoveColumn(new Board());
        }
        Gson gson = new Gson();
        String json = gson.toJson(player.getGenetics()[0]);
        FileWriter file = new FileWriter(" " + new Date().toString() + ".txt");
        file.write(json);
        file.flush();
        file.close();
    }

    public static void generateData() throws IOException {
        for (int j = 200; j < 202; j++) {
            ExecutorService service = Executors.newFixedThreadPool(8);
            List<String[]> results = new LinkedList<String[]>();
            for (int i = 0; i < 100; i++) {
                try {
                    results.add(service.submit(new Generator()).get());
                } catch (Exception e) {
                    System.exit(10);
                }
                System.out.println(i);
            }
            service.shutdown();
            BufferedWriter file = new BufferedWriter(new FileWriter("BoardsData/Data" + j + ".txt"), (int) Math.pow(2, 15));
            for (String[] result : results) {
                for (String map : result) {
                    file.write(map);
                }
            }
            file.flush();
            file.close();
        }
    }
}