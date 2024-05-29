package org.roulette.game.dto;

public class Bet {
    public String playerName;
    public String betType;
    public double amount;

    public Bet(String playerName, String betType, double amount) {
        this.playerName = playerName;
        this.betType = betType;
        this.amount = amount;
    }
}

