package org.music.abcod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LMBand {

    public static void main(String[] args) {

        ArrayList set = new ArrayList();
        int n = 8;//Integer.valueOf(args[0]);
        initialList(set, n);

        int k = 8;//Integer.valueOf(args[1]);
        ArrayList permutations = new ArrayList();
        printAllKLength(set, k, permutations);

        processPermutations(permutations, k);
    }

    private static void processPermutations(ArrayList permutations, int k) {
        // TODO Auto-generated method stub

        HashMap<Integer, Integer> dist = new HashMap<Integer, Integer>();
        initialDist(dist, k);

        System.out.println("input \t LIB \t LIB length \t LDB \t LDB length \t LMB length");

        for (int i = 0; i < permutations.size(); i++) {
            ArrayList perm = (ArrayList) permutations.get(i);


            ArrayList lis = LIS(perm);


            ArrayList lds = LDS(perm);

            int max = Math.max(lds.size(), lis.size());
            System.out.println(perm + " \t" + lis + "\t" + lis.size() + " \t" + lds + "\t" + lds.size() + "\t" + max);

            if (dist.containsKey(max)) {
                int count = dist.get(max);
                dist.put(max, count + 1);
            }

        }
        System.out.println("# permutations \t" + permutations.size());

        System.out.println("size \t count");
        for (Map.Entry e : dist.entrySet()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }
    }

    private static void initialDist(HashMap<Integer, Integer> dist, int k) {
        // TODO Auto-generated method stub
        for (int i = 1; i <= k; i++) {
            dist.put(i, 0);
        }
    }

    private static ArrayList LDS(ArrayList perm) {
        // TODO Auto-generated method stub
        ArrayList lds = new ArrayList();

        int[] L = new int[perm.size()];

        ArrayList S = new ArrayList();
        for (int i = 0; i < L.length; i++) {
            L[i] = 1;
            ArrayList list = new ArrayList();
            list.add(list.size(), perm.get(i));
            S.add(i, list);
        }

        int max = 0;
        for (int j = 1; j < perm.size(); j++) {
            int value_j = (Integer) perm.get(j);
            ArrayList list_j = (ArrayList) S.get(j);
            for (int k = 0; k < j; k++) {
                int value_k = (Integer) perm.get(k);
                ArrayList list_k = (ArrayList) S.get(k);
                if ((value_j < value_k) && L[k] + 1 > L[j]) {
                    list_j.clear();
                    list_j.addAll(list_k);
                    list_j.add(list_j.size(), value_j);
                    S.set(j, list_j);
                    L[j] = L[k] + 1;

                    max = Math.max(L[j], max);

                }
            }
        }

        //System.out.println("length of LIS \t"+max);
        for (int m = 0; m < L.length; m++) {
            int length = L[m];
            if (length == max)
                lds = (ArrayList) S.get(m);
            //System.out.println("\t"+S.get(m));
        }

        return lds;
    }

    private static ArrayList LIS(ArrayList perm) {
        // TODO Auto-generated method stub
        ArrayList lis = new ArrayList();

        int[] L = new int[perm.size()];

        ArrayList S = new ArrayList();
        for (int i = 0; i < L.length; i++) {
            L[i] = 1;
            ArrayList list = new ArrayList();
            list.add(list.size(), perm.get(i));
            S.add(i, list);
        }

        int max = 0;
        for (int j = 1; j < perm.size(); j++) {
            int value_j = (Integer) perm.get(j);
            ArrayList list_j = (ArrayList) S.get(j);
            for (int k = 0; k < j; k++) {
                int value_k = (Integer) perm.get(k);
                ArrayList list_k = (ArrayList) S.get(k);
                if ((value_j > value_k) && L[k] + 1 > L[j]) {
                    list_j.clear();
                    list_j.addAll(list_k);
                    list_j.add(list_j.size(), value_j);
                    S.set(j, list_j);
                    L[j] = L[k] + 1;

                    max = Math.max(L[j], max);

                }
            }
        }

        //System.out.println("length of LIS \t"+max);
        for (int m = 0; m < L.length; m++) {
            int length = L[m];
            if (length == max)
                lis = (ArrayList) S.get(m);
            //System.out.println("\t"+S.get(m));
        }

        return lis;
    }

    private static void initialList(ArrayList set, int n) {
        // TODO Auto-generated method stub
		/*
		set.add(0, 1);
		set.add(1, 3);
		set.add(2, 39);
		set.add(3, 57);
		set.add(4, 85);
		set.add(5, 24);
		set.add(6, 4);
		set.add(7, 92);
		set.add(8, 50);
		//set.add(9, 28);
		
		set.add(10, 389);
		set.add(11, 822);
		set.add(12, 8);
		*/

        int min = 0;

        int max = 10000;

        for (int i = 0; i < n; i++) {
            int rand = randInt(min, max);
            set.add(i, rand);
        }
    }

    private static int randInt(int min, int max) {
        // TODO Auto-generated method stub
        Random rand = new Random();

        int ranNum = rand.nextInt((max - min) + 1) + min;

        return ranNum;
    }

    private static void printAllKLength(ArrayList set, int k, ArrayList permutations) {
        // TODO Auto-generated method stub
        int n = set.size();
        ArrayList prefix = new ArrayList();
        printAllKLengthRec(set, prefix, n, k, permutations);
    }

    private static void printAllKLengthRec(ArrayList set, ArrayList prefix, int n, int k, ArrayList permutations) {
        // TODO Auto-generated method stub
        if (k == 0) {
            //System.out.println(prefix);
            permutations.add(permutations.size(), prefix);
            return;
        }

        for (int i = 0; i < n; ++i) {
            ArrayList newPrefix = new ArrayList();

            newPrefix.addAll(prefix);

            int value = (Integer) set.get(i);

            if (!newPrefix.contains(value)) {
                newPrefix.add(prefix.size(), value);

                printAllKLengthRec(set, newPrefix, n, k - 1, permutations);
            }
        }
    }

}
