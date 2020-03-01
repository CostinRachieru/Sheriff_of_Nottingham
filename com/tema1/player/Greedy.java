package com.tema1.player;

import com.tema1.common.Constants;
import com.tema1.goods.GoodsFactory;
import java.util.List;

public final class Greedy extends Player {

    public Greedy() {
        type = "GREEDY";
    }

    public void createBag(final int round) {
        int[] frequency = new int[Constants.NR_CARDS];
        int legalGoods = 0;
        int positionInHand = Constants.DEFAULT_ID;
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
            int chosenId = -1;
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
                        positionInHand = i;
                    }
                }
                bag.add(chosenId);
                penalty = GoodsFactory.getInstance().getGoodsById(chosenId).getPenalty();
            } else {
                // The case when he got no illegal goods.
                isBagLegal = 1;
            }
        }
        // The even round.
        if (round % 2 == 0) {
            if (isBagLegal == 1) {
                int maxProfit = 0;
                int chosenId = Constants.DEFAULT_ID;
                // Computes the most valuable illegal good to put in the bag.
                for (int i = 0; i < goodsInHand.size(); ++i) {
                    int goodId = goodsInHand.get(i);
                    if (goodId > Constants.MIN_ILLEGAL_ID && goodId < Constants.MAX_ILLEGAL_ID) {
                        int value = GoodsFactory.getInstance().getGoodsById(goodId).getProfit();
                        if (value > maxProfit) {
                            maxProfit = value;
                            chosenId = goodId;
                        }
                    }
                }
                if (chosenId > Constants.DEFAULT_ID) {
                    bag.add(chosenId);
                    penalty = GoodsFactory.getInstance().getGoodsById(chosenId).getPenalty();
                    isBagLegal = 0;
                }
            } else {
                declaredGood = 0;
                int maxProfit = 0;
                int chosenId = Constants.DEFAULT_ID;
                // Finds an illegal good to add to the bag.
                for (int i = 0; i < goodsInHand.size(); ++i) {
                    if (i != positionInHand) {
                        int goodId = goodsInHand.get(i);
                        if (goodId > Constants.MIN_ILLEGAL_ID && goodId < Constants.
                                MAX_ILLEGAL_ID) {
                            int value = GoodsFactory.getInstance().getGoodsById(goodId).getProfit();
                            if (value > maxProfit) {
                                maxProfit = value;
                                chosenId = goodId;
                            }
                        }
                    }
                }
                if (chosenId > Constants.DEFAULT_ID) {
                    bag.add(chosenId);
                    if (chosenId == 0) {
                        // It is declared that he has apples in the bag, so no penalty needed.
                        penalty -= 2;
                    }
                    penalty += GoodsFactory.getInstance().getGoodsById(chosenId).getPenalty();
                }
            }
        }
    }

    public void doSheriffJob(final List<Player> players, final List<Integer> assetOrder) {
        for (int i = 0; i  < players.size(); i++) {
            if (players.get(i) != this) {
                int bribe = players.get(i).bribe;
                if (bribe == 0) {
                    if (money > Constants.MIN_MONEY_SHERIFF) {
                        control(players.get(i), assetOrder);
                    } else {
                        noControl(players.get(i));
                    }
                } else {
                    noControlWithBribe(players.get(i));
                }
            }
        }
    }
}
