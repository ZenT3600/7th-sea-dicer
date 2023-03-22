package it.matteoleggio.seventhseadicer.dicer;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yonjuni on 5/6/15.
 */
public class Dicer {
    private static final SecureRandom random = new SecureRandom();

    private static int count(ArrayList<ArrayList<Integer>> s, boolean sum15) {
        int c = 0;
        for (int i = 0; i < s.size(); i++) {
            int subc = 0;
            for (int j = 0; j < s.get(i).size(); j++) {
                subc += s.get(i).get(j);
            }
            if (subc >= 15 && sum15) {
                c += 2;
            } else {
                c++;
            }
        }
        return c;
    }

    private static int sum(ArrayList<Integer> items) {
        int sum = 0;
        for (Integer item : items) {
            sum += item;
        }
        return sum;
    }

    public int[] rollDice(int poolSize, int faceNum){
        int[] dice = new int[poolSize];

        for (int i=0;i<dice.length;i++){
            dice[i] = random.nextInt(faceNum) +1;
        }

        return dice;
    }

    private ArrayList<ArrayList<ArrayList<String>>> findDisjointedPair(ArrayList<ArrayList<String>> pairs, int cap) {
        ArrayList<ArrayList<String>> valid = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < pairs.size(); i++) {
            ArrayList<String> pair = pairs.get(i);
            ArrayList<Integer> pairInt = new ArrayList<Integer>();
            for (int j = 0; j < pair.size(); j++) {
                pairInt.add(Integer.parseInt(pair.get(j).split("pos")[0]));
            }
            if (sum(pairInt) >= cap) valid.add(pair);
        }

        ArrayList<ArrayList<ArrayList<String>>> result = new ArrayList<ArrayList<ArrayList<String>>>();
        for (int i = 0; i < valid.size(); i++) {
            ArrayList<String> pair = valid.get(i);
            ArrayList<ArrayList<String>> currentPick = new ArrayList<ArrayList<String>>();
            currentPick.add(pair);
            for (int j = 0; j < valid.size(); j++) {
                if (i == j) continue;
                boolean ok = true;
                for (String dice : valid.get(j)) {
                    for (ArrayList<String> p : currentPick) if (p.contains(dice)) {
                        ok = false;
                        break;
                    };
                    if (!ok) break;
                }
                if (!ok) continue;
                currentPick.add(valid.get(j));
            }
            result.add(currentPick);
        }

        System.out.println(result);

        return result;
    }

    private ArrayList<ArrayList<Integer>> toInt(ArrayList<ArrayList<String>> in) {
        ArrayList<ArrayList<Integer>> out = new ArrayList<ArrayList<Integer>>();
        for (ArrayList<String> slist : in) {
            ArrayList<Integer> ilist = new ArrayList<Integer>();
            for (String el : slist) ilist.add(Integer.parseInt(el.split("pos")[0]));
            out.add(ilist);
        }
        return out;
    }

    public ArrayList<ArrayList<Integer>> findSuccess(int[] roll, int difficultyNumber, boolean sum15) {
        System.out.println(Arrays.toString(roll));
        if (roll.length < 2) {
            return new ArrayList<ArrayList<Integer>>();
        }

        // Try success with 2 dice
        ArrayList<String> roll2 = new ArrayList<String>();
        ArrayList<ArrayList<Integer>> dSuccess2 = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < roll.length; i++) {
            roll2.add(roll[i] + "pos" + i);
        }
        for (int i = 0; i < roll2.size(); i++) {
            String d = roll2.get(i);
            if (Integer.parseInt(d.split("pos")[0]) == 10) {
                ArrayList<Integer> p = new ArrayList<Integer>();
                p.add(Integer.parseInt(d.split("pos")[0]));
                dSuccess2.add(p);
                roll2.remove(i);
                i--;
            }
        }
        ArrayList<ArrayList<String>> possiblePairs = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < roll2.size(); i++) {
            String d = roll2.get(i);
            for (int j = 0; j < roll2.size(); j++) {
                if (i == j) continue;
                ArrayList<String> pair = new ArrayList<String>();
                pair.add(d);
                pair.add(roll2.get(j));
                possiblePairs.add(pair);
            }
        }
        ArrayList<ArrayList<ArrayList<String>>> disjointedMacroPairs = findDisjointedPair(possiblePairs, difficultyNumber);
        ArrayList<ArrayList<Integer>> tmpDSuccess2 = new ArrayList<ArrayList<Integer>>();
        int max = 0;
        for (ArrayList<ArrayList<String>> macroPair : disjointedMacroPairs) {
            if (macroPair.size() > max) {
                max = macroPair.size();
                tmpDSuccess2 = toInt(macroPair);
            }
        }
        dSuccess2.addAll(tmpDSuccess2);

        // Try success with 3 dice
        ArrayList<String> roll3 = new ArrayList<String>();
        for (int r : roll) {
            roll3.add(Integer.toString(r));
        }
        ArrayList<ArrayList<Integer>> dSuccess3 = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < roll3.size(); i++) {
            String d = roll3.get(i);
            if (Integer.parseInt(d.split("pos")[0]) == 10) {
                ArrayList<Integer> p = new ArrayList<Integer>();
                p.add(Integer.parseInt(d.split("pos")[0]));
                dSuccess3.add(p);
                roll3.remove(i);
                i--;
            }
        }
        ArrayList<ArrayList<String>> possiblePairs3 = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < roll3.size(); i++) {
            String d = roll2.get(i);
            for (int j = 0; j < roll3.size(); j++) {
                if (i == j) continue;
                String d2 = roll2.get(j);
                for (int l = 0; l < roll3.size(); l++) {
                    if (l == i) continue;
                    if (l == j) continue;
                    ArrayList<String> pair = new ArrayList<String>();
                    pair.add(d);
                    pair.add(d2);
                    pair.add(roll2.get(l));
                    possiblePairs3.add(pair);
                }
            }
        }
        System.out.println(possiblePairs3);
        ArrayList<ArrayList<ArrayList<String>>> disjointedMacroPairs3 = findDisjointedPair(possiblePairs3, difficultyNumber);
        ArrayList<ArrayList<Integer>> tmpDSuccess3= new ArrayList<ArrayList<Integer>>();
        int max3 = 0;
        for (ArrayList<ArrayList<String>> macroPair : disjointedMacroPairs3) {
            if (macroPair.size() > max3) {
                max3 = macroPair.size();
                tmpDSuccess3 = toInt(macroPair);
            }
        }
        dSuccess3.addAll(tmpDSuccess3);

        int cSuccess2 = count(dSuccess2, sum15);
        int cSuccess3 = count(dSuccess3, sum15);

        System.out.println(dSuccess2);
        System.out.println(dSuccess3);

        if (cSuccess2 == 0 && cSuccess3 == 0) {
            return new ArrayList<ArrayList<Integer>>();
        }
        if (cSuccess2 >= cSuccess3) {
            return dSuccess2;
        } else {
            return dSuccess3;
        }
    }
}
