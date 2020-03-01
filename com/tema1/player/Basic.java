package com.tema1.player;

import com.tema1.common.Constants;
import com.tema1.goods.GoodsFactory;
import java.util.List;

public final class Basic extends Player {

    public Basic() {
        type = "BASIC";
    }

    public void createBag(final int round) {
        int[] frequency = new int[Constants.NR_CARDS];
        int legalGoods = 0;
        // Computes the frequencies and the number of legal and illegal goods in hand.
        for (int i = 0; i < Constants.NR_CARDS; ++i) {
            int id = goodsInHand.get(i);
            if (id < Constants.MAX_LEGAL_ID) {
                legalGoods++;
                frequency[id]++;
            }
        }

        if (legalGoods > 0) {
            int maxFreq = 0;
            int maxProfit = 0;
            int chosenId = 0;
            isBagLegal = 1;
            for (int i = 0; i < Constants.NR_CARDS; ++i) {
                // Computes the maximum number of cards of the same type and which one will
                // the player chose to put in bag.
                if (frequency[i] > maxFreq) {
                    maxFreq = frequency[i];
                    maxProfit = GoodsFactory.getInstance().getGoodsById(i).getProfit();
                    chosenId = i;
                }
                if (frequency[i] == maxFreq) {
                    int value = GoodsFactory.getInstance().getGoodsById(i).getProfit();
                    if (value == maxProfit) {
                        chosenId = i;
                    } else {
                        if (value > maxProfit) {
                            chosenId = i;
                            maxProfit = value;
                        }
                    }
                }
            }
            if (maxFreq > Constants.MAX_CARDS_IN_BAG) {
                maxFreq = Constants.MAX_CARDS_IN_BAG;
            }
            for (int i = 0; i < maxFreq; ++i) {
                bag.add(chosenId);
            }
            declaredGood = chosenId;
            penalty = maxFreq * GoodsFactory.getInstance().getGoodsById(chosenId).getPenalty();
        } else {
            if (money > Constants.MIN_MONEY_ILLEGAL) {
                declaredGood = 0; // Declares Apples.
                isBagLegal = 0;
                int maxProfit = 0;
                int chosenId = 0;
                // Computes the most valuable illegal good to put in the bag.
                for (int i = 0; i < goodsInHand.size(); ++i) {
                    int goodId = goodsInHand.get(i);
                    int value = GoodsFactory.getInstance().getGoodsById(goodId).getProfit();
                    if (value > maxProfit) {
                        maxProfit = value;
                        chosenId = goodId;
                    }
                }
                bag.add(chosenId);
                penalty = GoodsFactory.getInstance().getGoodsById(chosenId).getPenalty();
            } else {
                isBagLegal = 1;
            }
        }
    }

    public void doSheriffJob(final List<Player> players, final List<Integer> assetOrder) {
        for (int i = 0; i  < players.size(); i++) {
            if (players.get(i) != this) {
                if (money > Constants.MIN_MONEY_SHERIFF) {
                    control(players.get(i), assetOrder);
                }
            }
        }
    }

}
