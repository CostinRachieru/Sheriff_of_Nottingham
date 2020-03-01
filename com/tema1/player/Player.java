package com.tema1.player;
import java.util.List;
import java.util.ArrayList;

import com.tema1.common.Constants;

public class Player {
    protected int[] goodsOnTable = new int[Constants.MAX_ID];
    protected List<Integer> bag = new ArrayList<Integer>();
    protected List<Integer> goodsInHand = new ArrayList<Integer>();
    protected String type;
    protected int declaredGood;
    private int bonus;
    protected int money;
    private int isSheriff;
    protected int isBagLegal;
    protected int penalty;
    protected int bribe;

    public Player() {
        this.goodsOnTable = new int[Constants.MAX_ID];
        this.bonus = 0;
        this.bribe = 0;
        this.penalty = 0;
        this.isBagLegal = 1;
        this.money = Constants.DEFAULT_MONEY;
        this.isSheriff = 0;
    }

    public final void setIsSheriff() {
        this.isSheriff = 1;
    }

    public final void clearIsSheriff() {
        this.isSheriff = 0;
    }

    // Function to actually get the cards from his hand and put them into the bag.
    public final void putAssetsInBag() {
        for (int i = 0; i < bag.size(); i++) {
            int id = bag.get(i);
            for (int j =  0; j < goodsInHand.size(); j++) {
                if (goodsInHand.get(j) == id) {
                    goodsInHand.remove(j);
                    break;
                }
            }
        }
    }

    public final void clearHand() {
        goodsInHand.clear();
    }

    public final void getAssets(final List<Integer> assetOrder) {
        if (isSheriff == 0) {
            for (int i = 0; i < Constants.NR_CARDS; i++) {
                goodsInHand.add(assetOrder.get(0));
                assetOrder.remove(0);
            }
        }
    }
    // The sheriff does not control the trader and does not get the bribe from him.
    protected final void noControl(final Player trader) {
        List<Integer> traderBag = trader.getBag();
        int size = traderBag.size();
        int goodId;
        for (int i = 0; i < size; ++i) {
            goodId = traderBag.remove(0);
            trader.goodsOnTable[goodId]++;
        }
        trader.bribe = 0;
        trader.penalty = 0;
    }
    // The seriff does not control the trader but get the bribe from him.
    protected final void noControlWithBribe(final Player trader) {
        List<Integer> traderBag = trader.getBag();
        int size = traderBag.size();
        int goodId;
        for (int i = 0; i < size; ++i) {
            goodId = traderBag.remove(0);
            trader.goodsOnTable[goodId]++;
        }
        money += trader.bribe;
        trader.money -= trader.bribe;
        trader.bribe = 0;
        trader.penalty = 0;
    }

    protected final void control(final Player trader, final List<Integer> assetOrder) {
        if (trader.isBagLegal == 1) {
            money -= trader.penalty;
            trader.money += trader.penalty;
            for (int i = 0; i < trader.bag.size(); i++) {
                trader.goodsOnTable[trader.bag.get(i)]++;
            }
            trader.bag.clear();
            trader.penalty = 0;
            trader.bribe = 0;
        } else {
            money += trader.penalty;
            trader.money -= trader.penalty;
            trader.penalty = 0;
            trader.bribe = 0;
            // Puts the confiscated goods back in the pack of cards.
            for (int i = 0; i < trader.bag.size(); i++) {
                if (trader.declaredGood != trader.bag.get(i)) {
                    assetOrder.add(trader.bag.get(i));
                    trader.bag.remove(i);
                    i--;
                }
            }
            int size = trader.bag.size();
            // Puts the declared items on the table.
            for (int i = 0; i < size; i++) {
                int good = trader.bag.remove(0);
                trader.goodsOnTable[good]++;
            }
        }
    }

    public final void addCredits(final int thisMuch) {
        money += thisMuch;
    }

    public final void addBonusAssets(final int id, final int multiplier) {
        goodsOnTable[id] += multiplier;
    }

    public void doSheriffJob(final List<Player> players, final List<Integer> assetOrder) {
    }

    public void createBag(final int round) {
    }

    public final int[] getGoodsOnTable() {
        return goodsOnTable;
    }

    public final int getMoney() {
        return money;
    }

    public final List<Integer> getBag() {
        return bag;
    }

    public final String getType() {
        return type;
    }
}
