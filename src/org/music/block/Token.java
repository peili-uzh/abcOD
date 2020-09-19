package org.music.block;

import org.music.data.ArtistData;

import java.util.*;

public class Token extends AbstractBlock {

    protected static ArrayList records;
    protected static String blockKey;

    public Token() {
    }

    ;

    public Token(ArrayList records, String blockKey) {

        this.records = records;
        this.blockKey = blockKey;

    }

    public static ArrayList tokenizeRecord(ArtistData artist) {
        // TODO Auto-generated method stub
        ArrayList tokenset = new ArrayList();
        int id = artist.getId();
        String value = "";
        if (blockKey.equals("name")) {
            value = artist.getName();
        } else if (blockKey.equals("real_name")) {
            value = artist.getReal_name();
        }

        if (!value.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(value);
            //System.out.println(id+"\t"+value);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                token = token.trim();
                if (!token.equals("")) {
                    tokenset.add(token);
                }
                //System.out.println("\t"+token);
            }

        }


        return tokenset;
    }

    public ArrayList tokenizeValues(String value) {
        // TODO Auto-generated method stub
        ArrayList tokenset = new ArrayList();


        if (!value.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(value);
            //System.out.println(id+"\t"+value);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                token = token.trim();
                if (!token.equals("")) {
                    tokenset.add(token);
                }
                //System.out.println("\t"+token);
            }

        }


        return tokenset;
    }

    public static String[] getBigram(String value) {
        ArrayList bigrams = new ArrayList();
        //System.out.println("ddd"+(value.length()-1)+"\t"+value);

        if (value.length() - 1 > 0) {
            String[] strs = new String[value.length() - 1];
            int index = 0;
            for (int i = 0; i < value.length() - 1; i++) {
                String substr = value.substring(i, i + 2);
                //System.out.println("-+ " + substr + "--" +i + "--" + (i+2));
                //System.out.println("substr: "+substr+"|");
                substr = substr.trim();
                //if(!substr.isEmpty()&&substr!=null){
                bigrams.add(bigrams.size(), substr);
                strs[index] = substr;
                index++;
                //}
            }
            if (strs[0] != null) {
                //System.out.println("+++"+strs[0]);

                //System.out.println(" \t"+bigrams.size()+"\t"+bigrams);
                //System.out.println("string length: "+strs.length);
                //for(int j=0; j<strs.length;j++){
                //System.out.println("\t\t"+strs[j]);
                //}

                Arrays.sort(strs);
            }


            return strs;
        } else {
            return null;
        }

    }

    public static HashMap<String, ArrayList> getTokenInvertedList() {
        HashMap<String, ArrayList> tokenlist = new HashMap();

        for (int i = 0; i < records.size(); i++) {
            ArtistData artist = (ArtistData) records.get(i);
            int id = artist.getId();
            ArrayList tokenset = tokenizeRecord(artist);
            //System.out.println(i+"\t"+tokenset);
            if (!tokenset.isEmpty()) {
                updateHashMap(tokenlist, tokenset, i);
            }


        }

        for (Map.Entry<String, ArrayList> e : tokenlist.entrySet()) {
            String token = e.getKey();
            ArrayList list = e.getValue();
            //System.out.println(token+": \t"+list);
        }

        return tokenlist;

    }

    private static void updateHashMap(HashMap<String, ArrayList> tokenlist,
                                      ArrayList tokenset, int id) {
        // TODO Auto-generated method stub
        Iterator it = tokenset.iterator();
        while (it.hasNext()) {
            String token = (String) it.next();
            if (!tokenlist.containsKey(token)) {
                ArrayList recordset = new ArrayList();
                recordset.add(id);
                tokenlist.put(token, recordset);
            } else {
                ArrayList recordset = tokenlist.get(token);
                recordset.add(id);
                tokenlist.put(token, recordset);
            }
        }
    }

}
