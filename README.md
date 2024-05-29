# Console Roulette Game

## Overview
Console Roulette is a command-line multiplayer version of the popular casino game, Roulette. Players can place bets on numbers, EVEN, or ODD, and the game will spin a roulette wheel every 30 seconds to determine the winning number. The game keeps track of each player's bets and winnings, displaying the results after each round.

## Features
- Multiple players can place bets concurrently.
- Supports bets on specific numbers (1-36), EVEN, and ODD.
- Spins the roulette wheel every 30 seconds.
- Calculates and displays results for each round.
- Tracks and displays the total amount each player has won and bet over the course of the game.

## Requirements
- Java 8 or higher

## How to Run
1. **Compile the Java Program:**
    ```sh
    javac Roulette.java
    ```

2. **Run the Program:**
    ```sh
    java Roulette
    ```

3. **Input File:**
    Create a file named `players.txt` in the same directory as your program. This file should contain the names of the players, optionally followed by their total win and total bet amounts, separated by commas.

    Example:
    ```
    Tiki_Monkey,1.0,2.0
    Barbara,2.0,1.0
    ```

4. **Placing Bets:**
    While the program is running, you can place bets by typing them into the console. Each bet should be in the format:
    ```
    <player_name> <bet_type> <amount>
    ```

    Examples:
    ```
    Tiki_Monkey 2 1.0
    Barbara EVEN 3.0
    ```

## Output
After each round, the game will display the winning number and the outcome of each bet. Additionally, it will display the total win and total bet amounts for each player.

Example output:

## Number: 4
```
Player Bet Outcome Winnings
Tiki_Monkey 2 LOSE 0.0
Barbara EVEN WIN 6.0
```
```
Player Total Win Total Bet
Tiki_Monkey 1.0 3.0
Barbara 8.0 4.0
```


## Source Code
### Player Class
```java
class Player {
    String name;
    double totalWin;
    double totalBet;

    public Player(String name, double totalWin, double totalBet) {
        this.name = name;
        this.totalWin = totalWin;
        this.totalBet = totalBet;
    }
}

class Bet {
    String playerName;
    String betType;
    double amount;

    public Bet(String playerName, String betType, double amount) {
        this.playerName = playerName;
        this.betType = betType;
        this.amount = amount;
    }
}

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Roulette {
    private static final Map<String, Player> players = new HashMap<>();
    private static final List<Bet> bets = Collections.synchronizedList(new ArrayList<>());
    private static final Random random = new Random();
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) {
        loadPlayers("players.txt");
        startBetting();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(Roulette::spinRoulette, 30, 30, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running.set(false);
            scheduler.shutdown();
        }));
    }

    private static void loadPlayers(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                double totalWin = parts.length > 1 ? Double.parseDouble(parts[1]) : 0.0;
                double totalBet = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.0;
                players.put(name, new Player(name, totalWin, totalBet));
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                        win = false;
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
```

# License
This project is licensed under the MIT License - see the LICENSE file for details.

# Author
Sethu Budaza

