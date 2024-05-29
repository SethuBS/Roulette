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

