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

    public ArrayList<ArrayList<Integer>> findSuccess(int[] roll, int difficultyNumber, boolean sum15) {
        System.out.println(Arrays.toString(roll));
        if (roll.length < 2) {
            return new ArrayList<ArrayList<Integer>>();
        }

        // Try success with 2 dice
        ArrayList<String> roll2 = new ArrayList<String>();
        for (int r : roll) {
            roll2.add(Integer.toString(r));
        }
        ArrayList<ArrayList<Integer>> dSuccess2 = new ArrayList<ArrayList<Integer>>();
        int cSuccess2 = 0;
        // Check for single 10s
        for (int i = 0; i < roll2.size(); i++) {
            String d = roll2.get(i);
            if (Integer.parseInt(d) == 10) {
                ArrayList<Integer> p = new ArrayList<Integer>();
                p.add(Integer.parseInt(d));
                dSuccess2.add(p);
		        cSuccess2++;
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
                        if (sum15 && (d1 + d2 >= 15)) {
                            ArrayList<Integer> p = new ArrayList<Integer>();
                            p.add(d1);
                            p.add(d2);
                            dSuccess2.add(p);
                            cSuccess2++;
                            cSuccess2++;
                        } else {
                            ArrayList<Integer> p = new ArrayList<Integer>();
                            p.add(d1);
                            p.add(d2);
                            dSuccess2.add(p);
                            cSuccess2++;
                        }
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
        ArrayList<ArrayList<Integer>> dSuccess3 = new ArrayList<ArrayList<Integer>>();
	    int cSuccess3 = 0;
        // Check for single 10s
        for (int i = 0; i < roll3.size(); i++) {
            String d = roll3.get(i);
            if (Integer.parseInt(d) == 10) {
                ArrayList<Integer> p = new ArrayList<Integer>();
                p.add(Integer.parseInt(d));
                dSuccess3.add(p);
		        cSuccess3++;
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
                            if (sum15 && (d1 + d2 + d3 >= 15)) {
                                ArrayList<Integer> p = new ArrayList<Integer>();
                                p.add(d1);
                                p.add(d2);
                                p.add(d3);
                                dSuccess3.add(p);
                                cSuccess3++;
                                cSuccess3++;
                            } else {
                                ArrayList<Integer> p = new ArrayList<Integer>();
                                p.add(d1);
                                p.add(d2);
                                p.add(d3);
                                dSuccess3.add(p);
                                cSuccess3++;
                            }
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
