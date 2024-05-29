package org.roulette.game.dto;

public class Player {
    public String name;
    public double totalWin;
    public double totalBet;

    public Player(String name, double totalWin, double totalBet) {
        this.name = name;
        this.totalWin = totalWin;
        this.totalBet = totalBet;
    }
}
