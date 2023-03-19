package it.matteoleggio.seventhseadicer.dicer;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yonjuni on 5/6/15.
 */
public class Dicer {
    private static final SecureRandom random = new SecureRandom();

    private static String join(String del, ArrayList<String> items) {
        StringBuilder sb = new StringBuilder();
        for (String item : items) {
            sb.append(item).append(del);
        }
        return sb.toString();
    }

    public int[] rollDice(int poolSize, int faceNum){
        int[] dice = new int[poolSize];

        for (int i=0;i<dice.length;i++){
            dice[i] = random.nextInt(faceNum) +1;
        }

        return dice;
    }

    public String findSuccess(int[] roll, int difficultyNumber) {
        System.out.println(Arrays.toString(roll));
        if (roll.length < 2) {
            return "Could not calculate success count...";
        }

        // Try success with 2 dice
        ArrayList<String> roll2 = new ArrayList<String>();
        for (int r : roll) {
            roll2.add(Integer.toString(r));
        }
        ArrayList<String> dSuccess2 = new ArrayList<String>();
        // Check for single 10s
        for (int i = 0; i < roll2.size(); i++) {
            String d = roll2.get(i);
            if (Integer.parseInt(d) >= 10) {
                dSuccess2.add("[" + d + "] >= " + difficultyNumber);
                roll2.remove(i);
                i--;
            }
        }
        for (int i = 0; i < roll2.size(); i++) {
            System.out.println(roll2);
            try {
                int d1 = Integer.parseInt(roll2.get(i));
                for (int j = 0; j < roll2.size(); j++) {
                    if (j == i) continue;
                    int d2 = Integer.parseInt(roll2.get(j));
                    if (d1 + d2 >= difficultyNumber) {
                        dSuccess2.add("[" + d1 + ", " + d2 + "] >= " + difficultyNumber);
                        roll2.set(i, "-9999");
                        roll2.set(j, "-9999");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println();

        // Try success with 3 dice
        ArrayList<String> roll3 = new ArrayList<String>();
        for (int r : roll) {
            roll3.add(Integer.toString(r));
        }
        ArrayList<String> dSuccess3 = new ArrayList<String>();
        // Check for single 10s
        for (int i = 0; i < roll3.size(); i++) {
            String d = roll3.get(i);
            if (Integer.parseInt(d) >= difficultyNumber) {
                dSuccess3.add("[" + d + "] >= " + difficultyNumber);
                roll3.remove(i);
                i--;
            }
        }
        boolean tobreak = false;
        for (int i = 0; i < roll3.size(); i++) {
            System.out.println(roll3);
            try {
                int d1 = Integer.parseInt(roll3.get(i));
                for (int j = 0; j < roll3.size(); j++) {
                    if (j == i) continue;
                    int d2 = Integer.parseInt(roll3.get(j));
                    for (int l = 0; l < roll3.size(); l++) {
                        if (l == i || l == j) continue;
                        int d3 = Integer.parseInt(roll3.get(l));
                        if (d1 + d2 + d3 >= difficultyNumber) {
                            dSuccess3.add("[" + d1 + ", " + d2 + ", " + d3 + "] >= " + difficultyNumber);
                            roll3.set(i, "-9999");
                            roll3.set(j, "-9999");
                            roll3.set(l, "-9999");
                            tobreak = true;
                            break;
                        }
                    }
                    if (tobreak) break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println();

        if (dSuccess2.size() == 0 && dSuccess3.size() == 0) {
            return "Failing roll";
        }
        if (dSuccess2.size() >= dSuccess3.size()) {
            return join("\n", dSuccess2);
        } else {
            return join("\n", dSuccess3);
        }
    }
}
