package org.music.block;

import org.music.connection.ConnectionPool;
import org.music.data.ArtistData;
import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;


public class AbstractBlock {

    private ArtistData artist;
    private int id;
    private int cluster_id;
    private String name;
    private String profile;
    private String real_name;
    private String track;
    private ArrayList tracklist;
    private String source;

    protected ArrayList records;

    public ArrayList getRecords() {
        return records;
    }

    public void setRecords(ArrayList records) {
        this.records = records;
    }

    protected String blockKey;

    protected Evaluation evaluation;

    protected HashMap<Integer, Block> blocks;
    protected HashMap<Integer, ArrayList> invertedList;

    protected double th1;
    protected double th2;


    public static String cleanValue(String value) {

        value = value.replace("&", "");
        value = value.replace("!", "");
        value = value.replace("'", "");
        value = value.replace(":", "");
        value = value.replace(".", "");
        value = value.replace("\"", "");
        value = value.replace("-", " ");
        value = value.replace("/", "");
        value = value.replace("=", "");
        value = value.replace("?", "");
        value = value.replace("(", "");
        value = value.replace(")", "");
        value = value.replace(",", "");
        value = value.replace("#", "");
        value = value.replace("$", "");
        value = value.replace("*", "");
        value = value.replace("%", "");
        value = value.replace("+", "");
        value = value.replace("\\", "");
        value = value.toLowerCase();
        value = value.replace("[", "");
        value = value.replace("]", "");
        value = value.replace("@", "");
        value = value.replace("1", "");
        value = value.replace("2", "");
        value = value.replace("3", "");
        value = value.replace("4", "");
        value = value.replace("5", "");
        value = value.replace("6", "");
        value = value.replace("7", "");
        value = value.replace("8", "");
        value = value.replace("9", "");
        value = value.replace("0", "");
        value = value.trim();


        return value;

    }

    protected void blockStatistics(HashMap<Integer, Block> blocks) {
        int maxBlockSize = 0;
        int minBlockSize = 1000000;
        int numRecords = 0;
        int numSingleBlock = 0;
        for (Map.Entry<Integer, Block> e : blocks.entrySet()) {
            Block block = e.getValue();
            HashSet entities = new HashSet(block.getEntities());
            int localSize = entities.size();
            numRecords += localSize;
            if (localSize == 1)
                numSingleBlock += 1;
            if (localSize > maxBlockSize)
                maxBlockSize = localSize;
            if (localSize < minBlockSize)
                minBlockSize = localSize;
        }
        double avgSize = Double.valueOf(numRecords) / Double.valueOf(blocks.size());

        System.out.println("# blocks \t block size \t single-size block \t average block size");
        System.out.println(blocks.size() + "\t" + "[" + minBlockSize + ", " + maxBlockSize + "] \t" + numSingleBlock + "\t" + avgSize);
    }

    protected void invertedListStatistics(HashMap<Integer, ArrayList> invertedList) {
        int numBlocks = 0;
        int maxBlocks = 0;
        int minBlocks = 1000000;
        double avgBlocks = 0;

        for (Map.Entry<Integer, ArrayList> e : invertedList.entrySet()) {
            HashSet blocks = new HashSet(e.getValue());
            numBlocks += blocks.size();
            if (maxBlocks < blocks.size())
                maxBlocks = blocks.size();
            if (minBlocks > blocks.size())
                minBlocks = blocks.size();
        }
        avgBlocks = Double.valueOf(numBlocks) / Double.valueOf(invertedList.size());
        System.out.println("# blocks a record belongs to \t avg # blocks a reocrd belongs to");
        System.out.println("[" + minBlocks + ", " + maxBlocks + "] \t" + avgBlocks);
    }

    protected String getValueByBlockKey(ArtistData artist, String blockKey) {
        String value = "";
        if (blockKey.equals("name")) {
            value = artist.getName();
        } else if (blockKey.equals("real_name")) {
            value = artist.getReal_name();
        }
        return value;
    }

    protected String array2String(String[] array) {
        String str = "";
        str = Arrays.toString(array);
        return str;
    }


    public HashMap<Integer, Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(HashMap<Integer, Block> blocks) {
        this.blocks = blocks;
    }

    public HashMap<Integer, ArrayList> getInvertedList() {
        return invertedList;
    }

    public void setInvertedList(HashMap<Integer, ArrayList> invertedList) {
        this.invertedList = invertedList;
    }

    public void queryRecords(ArrayList records, String sql) throws Exception {

        Connection con = ConnectionPool.getConnection();

        if (!con.isClosed()) {
            int i = 0;
            Statement st = con.createStatement();
            System.out.println(sql);
            ResultSet result = st.executeQuery(sql);

            //select id, cluster_id, name, profile, real_name, tracks, source from artist_merged where name<>'' and profile<>'' and real_name<>'' and tracks<>''
            while (result.next()) {
                i++;
                id = result.getInt(1);
                cluster_id = result.getInt(2);
                name = result.getString(3);
                name = cleanValue(name);
                profile = result.getString(4);
                real_name = result.getString(5);
                real_name = cleanValue(real_name);
                track = result.getString(6);
                source = result.getString(7);
                //System.out.println(i+"\t"+id+"\t"+name+"\t"+cluster_id);

                tracklist = new ArrayList();
                if (track != null)
                    convert2List(track, tracklist);
                //System.out.println("\t"+tracklist.size());
                artist = new ArtistData(id, cluster_id, name, profile, real_name, tracklist, source);
                records.add(records.size(), artist);

            }

            st.close();
        }

        ConnectionPool.putConnection(con);

    }

    private ArrayList convert2List(String str, ArrayList list) {
        // TODO Auto-generated method stub
        //System.out.println(str);
        String[] split = str.split("\\^\\|");
        for (int i = 0; i < split.length; i++) {
            String title = split[i];
            title = cleanValue(title);
            //System.out.println("\t"+i+"\t"+title);
            if (!title.equals("")) {
                list.add(title);
            }

        }
        return list;
    }

}
