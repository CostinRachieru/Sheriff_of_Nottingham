package com.tema1.main;

import java.util.List;
import java.util.ArrayList;

import com.tema1.player.Player;
import com.tema1.player.Basic;
import com.tema1.player.Bribed;
import com.tema1.player.Greedy;
import com.tema1.player.LeaderBoard;



public final class Game {
    private int nrRounds;
    private int nrPlayers;
    private int nrAssets;
    private final List<Integer> assetOrder;
    private final List<String> playersType;
    private final List<Player> players = new ArrayList<Player>();

    public Game(final int rounds, final List<String> playersType, final List<Integer> assets) {
        this.nrRounds = rounds;
        this.playersType = playersType;
        this.assetOrder = assets;
        nrPlayers = playersType.size();
        nrAssets = assets.size();
        for (int i = 0; i < nrPlayers; ++i) {
            String type;
            type = playersType.get(i);
            if (type.equals("bribed")) {
                players.add(new Bribed());
            }
            if (type.equals("basic")) {
                players.add(new Basic());
            }
            if (type.equals("greedy")) {
                players.add(new Greedy());
            }
        }

    }

    public void sheriffOfNottingam() {
        for (int round = 0; round < nrRounds; ++round) {
            for (int sheriff = 0; sheriff < nrPlayers; ++sheriff) {
                for (int i = 0; i < nrPlayers; ++i) {
                    if (i == sheriff) {
                        players.get(i).setIsSheriff();
                    } else {
                        players.get(i).clearIsSheriff();
                        players.get(i).getAssets(assetOrder);
                        players.get(i).createBag(round + 1);
                        if (!players.get(i).getType().equals("bribed")) {
                            players.get(i).putAssetsInBag();
                        }
                        players.get(i).clearHand();
                    }
                }
                players.get(sheriff).doSheriffJob(players, assetOrder);
            }
        }
        LeaderBoard leaderBoard = new LeaderBoard(nrPlayers);
        leaderBoard.updateMoneyAndGoods(players);
        leaderBoard.computeKingsAndQueens(players);
        leaderBoard.doBonuses(players);
        leaderBoard.computeScoreBoard(players);
        leaderBoard.printLeaderBoard(players);
    }

}
