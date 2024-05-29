package org.roulette.game;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.roulette.game.dto.Bet;
import org.roulette.game.dto.Player;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class Roulette {
    private static final Logger logger = LogManager.getLogger(Roulette.class);
    private static final Map<String, Player> players = new HashMap<>();
    private static final List<Bet> bets = Collections.synchronizedList(new ArrayList<>());
    private static final Random random = new Random();
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) {
        loadPlayers();
        startBetting();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(Roulette::spinRoulette, 30, 30, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running.set(false);
            scheduler.shutdown();
        }));
    }

    private static void loadPlayers() {
        try (BufferedReader br = new BufferedReader(new FileReader("players.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                double totalWin = parts.length > 1 ? Double.parseDouble(parts[1]) : 0.0;
                double totalBet = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.0;
                players.put(name, new Player(name, totalWin, totalBet));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void startBetting() {
        Thread bettingThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running.get()) {
                String input = scanner.nextLine();
                String[] parts = input.split(" ");
                if (parts.length == 3) {
                    String playerName = parts[0];
                    String betType = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    synchronized (bets) {
                        bets.add(new Bet(playerName, betType, amount));
                    }
                }
            }
        });
        bettingThread.start();
    }

    private static void spinRoulette() {
        int winningNumber = random.nextInt(37);
        System.out.println("Number: " + winningNumber);
        Map<String, Double> winnings = new HashMap<>();

        synchronized (bets) {
            for (Bet bet : bets) {
                boolean win = false;
                double multiplier = 0.0;
                if (bet.betType.equalsIgnoreCase("EVEN") && winningNumber != 0 && winningNumber % 2 == 0) {
                    win = true;
                    multiplier = 2.0;
                } else if (bet.betType.equalsIgnoreCase("ODD") && winningNumber % 2 != 0) {
                    win = true;
                    multiplier = 2.0;
                } else {
                    try {
                        int betNumber = Integer.parseInt(bet.betType);
                        if (betNumber == winningNumber) {
                            win = true;
                            multiplier = 36.0;
                        }
                    } catch (NumberFormatException e) {
                    }
                }

                double winAmount = win ? bet.amount * multiplier : 0.0;
                winnings.put(bet.playerName, winnings.getOrDefault(bet.playerName, 0.0) + winAmount);

                System.out.printf("%s %s %s %.2f\n", bet.playerName, bet.betType, win ? "WIN" : "LOSE", winAmount);
            }
            bets.clear();
        }

        // Update total winnings and bets for each player
        for (Map.Entry<String, Double> entry : winnings.entrySet()) {
            Player player = players.get(entry.getKey());
            double winAmount = entry.getValue();
            player.totalWin += winAmount;
            player.totalBet += bets.stream().filter(bet -> bet.playerName.equals(entry.getKey())).mapToDouble(bet -> bet.amount).sum();
        }

        // Print totals
        System.out.println("Player       Total Win  Total Bet");
        System.out.println("---");
        for (Player player : players.values()) {
            System.out.printf("%s        %.2f        %.2f\n", player.name, player.totalWin, player.totalBet);
        }
    }
}