package com.InfinityRaider.AgriCraft.farming.mutation.statcalculator;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;

public class StatCalculatorHardcore extends StatCalculatorBase {
    /**
     * calculates the new stats based on an input stat, the nr of neighbours and a divisor
     */
    protected int calculateStats(int input, int neighbours, int divisor) {
        /*
        1 parent: 3/4 decrement, 1/4 nothing
        2 parents: 2/4 decrement, 1/4 nothing, 1/4 increment
        3 parents, 1/4 decrement, 2/4 nothing, 1/4 increment
        4 parents: 1/4 decrement, 1/4 nothing, 2/4 increment
        */
        if(neighbours==1 && ConfigurationHandler.singleSpreadsIncrement) {
            neighbours = 2;
        }
        int newStat = getAction(neighbours).apply(input)/ divisor;
        return Math.max(1, Math.min(newStat, ConfigurationHandler.cropStatCap));
    }

    private Action getAction(int count) {
        int totalWeight = 0;
        Action[] actions = Action.values();
        for(Action action:actions) {
            totalWeight = totalWeight + action.getWeight(count);
        }
        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for(int i=0;i<actions.length;i++) {
            random = random - actions[i].getWeight(count);
            if (random <= 0.0D) {
                randomIndex = i;
                break;
            }
        }
        return actions[randomIndex];
    }

    private enum Action {
        DECREMENT(-1, new int[] {3, 2, 1, 1}),
        NOTHING(0, new int[] {1, 1, 2, 1}),
        INCREMENT(1, new int[] {0, 1, 1, 2});

        private final int incr;
        private final int[] weights;

        Action(int incr, int[] weights) {
            this.incr = incr;
            this.weights = weights;
        }

        public int getWeight(int count) {
            count--;
            count = Math.max(Math.min(count, weights.length-1), 0);
            return weights[count];
        }

        public int apply(int input) {
            return input + incr;
        }
    }
}
