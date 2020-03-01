package com.tema1.player;

import com.tema1.common.Constants;
import com.tema1.goods.GoodsFactory;
import java.util.List;

public final class Bribed extends Player {
    public Bribed() {
        type = "BRIBED";
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
        // If there are only legal cards or the player doesn t have enough money he uses
        // the basic strategy.
        if (legalGoods == Constants.NR_CARDS || money <= Constants.MIN_BRIBE) {
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
        } else {  // There are illegal cards too.
            isBagLegal = 0;
            int illegalInBag = 0;
            declaredGood = 0;
            // Extracts the most valuable card one by one and puts it in bag if the player
            // has enough money.
            for (int i = 0; i < Constants.MAX_CARDS_IN_BAG; ++i) {
                if (goodsInHand.size() == 0) {
                    break;
                }
                GoodsFactory goodsFactory = GoodsFactory.getInstance();
                int maxValue = 0;
                int chosenId = -1;
                int position = -1;
                for (int j = 0; j < goodsInHand.size(); ++j) {
                    int value = goodsFactory.getGoodsById(goodsInHand.get(j)).getProfit();
                    if (value > maxValue) {
                        maxValue = value;
                        chosenId = goodsInHand.get(j);
                        position = j;
                    } else {
                        if (value == maxValue) {
                            if (chosenId < goodsInHand.get(j)) {
                                chosenId = goodsInHand.get(j);
                                position = j;
                            }
                        }
                    }
                }
                // Puts the illegal card in bag if the player got enough money.
                if (chosenId > Constants.MIN_ILLEGAL_ID && chosenId < Constants.MAX_ILLEGAL_ID) {
                    if (penalty + Constants.PENALTY_ILLEGAL < money) {
                        bag.add(chosenId);
                        goodsInHand.remove(position);
                        illegalInBag++;
                        penalty += Constants.PENALTY_ILLEGAL;
                    } else { // Throw away the card if he does not have money for it.
                        i--;
                        goodsInHand.remove(position);
                    }
                } else { // Puts the legal card in bag if the player got enough money.
                    if (penalty + Constants.PENALTY_LEGAL < money) {
                        bag.add(chosenId);
                        goodsInHand.remove(position);
                        penalty += Constants.PENALTY_LEGAL;
                        if (chosenId == 0) {
                            penalty -= 2;
                        }
                    } else { // Throw away the card if he does not have money for it.
                        i--;
                        goodsInHand.remove(position);
                    }
                }
            }
            if (illegalInBag == 0) {
                isBagLegal = 1;
            }
            if (illegalInBag > 0 && illegalInBag <= Constants.MAX_CARDS_MIN_BRIBE) {
                bribe = Constants.MIN_BRIBE;
            }
            if (illegalInBag > 2) {
                bribe = Constants.MAX_BRIBE;
            }
        }
    }

    public void doSheriffJob(final List<Player> players, final List<Integer> assetOrder) {
        int leftPlayer = Constants.DEFAULT_ID;
        int rightPlayer = Constants.DEFAULT_ID;
        for (int i = 0; i < players.size(); ++i) {
            if (players.get(i) == this) {
                leftPlayer = i - 1;
                rightPlayer = i + 1;
                break;
            }
        }
        if (leftPlayer == -1) {
            leftPlayer = players.size() - 1;
        }
        if (rightPlayer == players.size()) {
            rightPlayer = 0;
        }
        // Controls the left and right players if he got the money.
        if (money > Constants.MIN_MONEY_SHERIFF) {
            control(players.get(leftPlayer), assetOrder);
        } else {
            noControl(players.get(leftPlayer));
        }
        if (money > Constants.MIN_MONEY_SHERIFF) {
            control(players.get(rightPlayer), assetOrder);
        } else {
            noControl(players.get(rightPlayer));
        }
        // Get the bribes from the other players.
        for (int i = 0; i < players.size(); ++i) {
            if (i != leftPlayer && i != rightPlayer) {
                if (players.get(i) != this) {
                    noControlWithBribe(players.get(i));
                }
            }
        }
    }
}
