package com.tema1.player;

import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.IllegalGoods;
import com.tema1.goods.LegalGoods;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class LeaderBoard {
    private final int[] kingPerGood = new int[Constants.MAX_LEGAL_ID];
    private final int[] maxForKing = new int[Constants.MAX_LEGAL_ID];
    private final int[] queenPerGood = new int[Constants.MAX_LEGAL_ID];
    private final int[] maxForQueen = new int[Constants.MAX_LEGAL_ID];
    private final int[] scoreBoard;
    public LeaderBoard(final int nrPlayers) {
        Arrays.fill(kingPerGood, Constants.DEFAULT_UNUSED);
        Arrays.fill(queenPerGood, Constants.DEFAULT_UNUSED);
        Arrays.fill(maxForKing, Constants.DEFAULT_MAX);
        Arrays.fill(maxForQueen, Constants.DEFAULT_MAX);
        scoreBoard = new int[nrPlayers];
        Arrays.fill(scoreBoard, Constants.DEFAULT_MAX);
    }
    // Prints the ScoreBoard at the final of the game.
    public void printLeaderBoard(final List<Player> players) {
        for (int i = 0; i < players.size(); ++i) {
            System.out.println(scoreBoard[i] + " " + players.get(scoreBoard[i]).getType()
                    + " " + players.get(scoreBoard[i]).getMoney());
        }
    }
    // Makes an array with the ids of the players sorted by their amount of money.
    public void computeScoreBoard(final List<Player> players) {
        int nrPlayers = players.size();
        for (int i = 0; i < nrPlayers; ++i) {
            // Gets the number one player first.
            if (i == 0) {
                int max = 0;
                for (int j = 0; j < nrPlayers; ++j) {
                    if (players.get(j).getMoney() > max) {
                        scoreBoard[i] = j;
                        max = players.get(j).getMoney();
                    }
                }
            } else {
                // Get the other players one by one.
                for (int j = 0; j < nrPlayers; ++j) {
                    boolean isAlready = false;
                    // Checks if the player is already in the ScoreBoard.
                    for (int k = 0; k < i; k++) {
                        if (scoreBoard[k] == j) {
                            isAlready = true;
                        }
                    }
                    // Put the player in the ScoreBoard.
                    if (!isAlready) {
                        if (players.get(j).getMoney() <= players.
                                get(scoreBoard[i - 1]).getMoney()) {
                            if (scoreBoard[i] == -1) {
                                scoreBoard[i] = j;
                            } else {
                                if (players.get(j).getMoney() > players.
                                        get(scoreBoard[i]).getMoney()) {
                                    scoreBoard[i] = j;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // Makes two arrays in which are stored the king and queen for each good.
    public void computeKingsAndQueens(final List<Player> players) {
        for (int i = 0; i < Constants.MAX_LEGAL_ID; ++i) {
            // Checks every player for each good to see who has the most of each.
            for (int j = 0; j < players.size(); ++j) {
                int[] goodsOnTable = players.get(j).getGoodsOnTable();
                if (goodsOnTable[i] > 0) {
                    if (goodsOnTable[i] > maxForKing[i]) {
                        maxForQueen[i] = maxForKing[i];
                        queenPerGood[i] = kingPerGood[i];
                        maxForKing[i] = goodsOnTable[i];
                        kingPerGood[i] = j;
                    } else {
                        if (goodsOnTable[i] > maxForQueen[i]) {
                            maxForQueen[i] = goodsOnTable[i];
                            queenPerGood[i] = j;
                        }
                    }
                }
            }
        }
    }
    // Give the king and queen bonuses to the players.
    public void doBonuses(final List<Player> players) {
        for (int i = 0; i < Constants.MAX_LEGAL_ID; ++i) {
            if (kingPerGood[i] < Constants.DEFAULT_UNUSED) {
                LegalGoods good = (LegalGoods) GoodsFactory.getInstance().getGoodsById(i);
                int value = good.getKingBonus();
                players.get(kingPerGood[i]).addCredits(value);
            }
            if (queenPerGood[i] < Constants.DEFAULT_UNUSED) {
                LegalGoods good = (LegalGoods) GoodsFactory.getInstance().getGoodsById(i);
                int value = good.getQueenBonus();
                players.get(queenPerGood[i]).addCredits(value);
            }
        }
    }
    // Gives to players the goods from illegal cards bonuses and the money for their goods
    // on the table.
    public void updateMoneyAndGoods(final List<Player> players) {
        for (int i = 0; i < players.size(); ++i) {
            Player player = players.get(i);
            int[] goodsOnTable = player.getGoodsOnTable();
            // The good is an illegal one and has other bonuses stored in a hashmap.
            for (int j = Constants.MIN_ILLEGAL_ID + 1; j < Constants.MAX_ILLEGAL_ID; ++j) {
                if (goodsOnTable[j] != 0) {
                    IllegalGoods aux = (IllegalGoods) GoodsFactory.getInstance().getGoodsById(j);
                    // Gets the hashmap with bonuses and iterates through it's keys to get
                    // the quantity of each bonus good.
                    Map<Goods, Integer> map = aux.getIllegalBonus();
                    for (Goods good : map.keySet()) {
                        player.addBonusAssets(good.getId(), goodsOnTable[j] * map.get(good));
                    }
                    int value = GoodsFactory.getInstance().getGoodsById(j).getProfit();
                    player.addCredits(goodsOnTable[j] * value);
                }
            }
            // Gives players the money for their legal goods.
            for (int j = 0; j < Constants.MAX_LEGAL_ID; ++j) {
                if (goodsOnTable[j] != 0) {
                    int value = GoodsFactory.getInstance().getGoodsById(j).getProfit();
                    player.addCredits(goodsOnTable[j] * value);
                }
            }
        }
    }
}
