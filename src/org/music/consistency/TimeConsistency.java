package org.music.consistency;

import org.music.block.AbstractBlock;
import org.music.connection.ConnectionPool;
import org.music.data.ReleaseLabel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TimeConsistency {

    public static void main(String[] args) throws Exception {

        String sql = "select  r.id , rn.name, a.name, rc.date_year, ln.name, dr.title, dr.country, dr.date, drl.label_name " +
                "from l_release_url l, release r, url u, release_name rn, release_country rc, discogs_release dr, discogs_release_label drl, release_label rl, label la , label_name ln, area a " +
                "where l.link = '6301' and r.id = entity0 and u.id = entity1 and rn.id = r.name and rc.release=r.id and drl.release_id=dr.id and rl.release=r.id and la.id = rl.label and la.name = ln.id " +
                "and substring(url from 32  for char_length(url)-1) = cast(dr.id as character varying) and rc.country = a.id";

        String sql1 = "select rl.id, r.title as release, r.genres, r.styles, r.country, date, ra.name as artist_name, rl.label_name, rl.catno, rf.name as format, rf.qty, rf.description, re.name as extra_artist, l.id as cluster_id " +
                "from discogs_release r, discogs_release_artist ra, discogs_release_label rl, discogs_label l, discogs_release_format rf, discogs_release_extraartist re " +
                "where ra.release_id=r.id  and date <>'' and rf.release_id = r.id and re.release_id = r.id " +
                "and r.id=rl.release_id and rl.label_name = l.name and (  l.name = 'Cisco Music') and date<>'?'  order by l.id, date, catno";

        String sql2 = "select * from inter_music_release_full where date_year >0 limit 100";

        checkConsistency(sql);
        //ArrayList records = new ArrayList();
        LinkedHashMap<Integer, ReleaseLabel> records = new LinkedHashMap<Integer, ReleaseLabel>();
        records = checkFrequency(sql1);

        printRecords(records);

        ArrayList releases = new ArrayList();
        releases = getinterRelease(sql2);
    }

    public static ArrayList getinterRelease(String sql) throws Exception {
        // TODO Auto-generated method stub
        ArrayList releases = new ArrayList();
        LinkedHashMap<Integer, ReleaseLabel> temp = new LinkedHashMap<Integer, ReleaseLabel>();

        Connection con = ConnectionPool.getConnection();
        LinkedHashMap<Integer, ReleaseLabel> records = new LinkedHashMap<Integer, ReleaseLabel>();

        if (!con.isClosed()) {
            int i = 0;
            Statement st = con.createStatement();
            System.out.println(sql);
            ResultSet result = st.executeQuery(sql);


            //select id, release, country, date, label, format, catalog_number, artist, extra_artist, discogs_id
            // 	 1, 	2,			3, 		4, 		5, 		6, 		7, 				8, 			9		10
            while (result.next()) {
                int id = result.getInt(1);
                String release = result.getString(2);
                String country = result.getString(3);
                String year = result.getString(4);
                String label = result.getString(5);
                String format = result.getString(6);
                String catno = result.getString(7);
                String artist = result.getString(8);
                String extra = result.getString(9);
                int discogsid = result.getInt(10);
                if (extra == null) {
                    extra = "";
                }
                if (format == null)
                    format = "";
                if (catno == null)
                    catno = "";

                label = AbstractBlock.cleanValue(label);
                release = AbstractBlock.cleanValue(release);
                artist = AbstractBlock.cleanValue(artist);
                extra = AbstractBlock.cleanValue(extra);
                country = AbstractBlock.cleanValue(country);

                //System.out.println(i+"\t"+id+"\t"+release+"\t"+label+"\t"+country+"\t"+year+"\t"+artist+"\t"+extra+"\t"+catno+"\t"+format+"\t"+discogsid);

                if (temp.containsKey(id)) {
                    ReleaseLabel relabel = temp.get(id);
                    String artists = relabel.getStrartist();
                    if (!artists.contains(artist)) {
                        artists += ", " + artist;
                        relabel.setStrartist(artists);
                    }

                    String extra_artists = relabel.getStrextrartist();
                    if (!extra_artists.contains(extra)) {
                        extra_artists += ", " + extra;
                        relabel.setStrextrartist(extra_artists);
                    }
                } else {
                    ArrayList list = new ArrayList();
                    ReleaseLabel releaselabel = new ReleaseLabel();
                    releaselabel.setStrartist(artist);
                    releaselabel.setCatno(catno);
                    releaselabel.setCluster_id(discogsid);
                    releaselabel.setCountry(country);
                    if (year != null)
                        releaselabel.setDate(Integer.parseInt(year));
                    releaselabel.setStrextrartist(extra);
                    releaselabel.setFormat(format);
                    releaselabel.setFormat_description("");
                    releaselabel.setGenreslist(list);
                    releaselabel.setId(id);
                    releaselabel.setLabel(label);
                    releaselabel.setQty("");
                    releaselabel.setRelease(release);
                    releaselabel.setStyleslist(list);

                    temp.put(id, releaselabel);
                }


                i++;

            }
            //System.out.println(i);
            st.close();
        }

        ConnectionPool.putConnection(con);

        for (Map.Entry<Integer, ReleaseLabel> e : temp.entrySet()) {
            int id = e.getKey();
            ReleaseLabel relabel = e.getValue();
            //System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());
            releases.add(releases.size(), relabel);
        }
        System.out.println("musicbrainz label \t" + releases.size());
        return releases;
    }

    private static void printRecords(HashMap<Integer, ReleaseLabel> records) {
        // TODO Auto-generated method stub
        for (Map.Entry<Integer, ReleaseLabel> e : records.entrySet()) {
            int id = e.getKey();
            ReleaseLabel relabel = e.getValue();

            System.out.println(id + "\t" + relabel.getRelease() + "\t" + relabel.getLabel() + "\t" + relabel.getCountry() + "\t" + relabel.getDate() + "\t" + relabel.getGenres() + "\t" + relabel.getStyles() + "\t" + relabel.getArtist() + "\t" + relabel.getExtra_artist() + "\t" + relabel.getCatno() + "\t" + relabel.getFormat() + "\t" + relabel.getQty() + "\t" + relabel.getFormat_description() + "\t" + relabel.getCluster_id());
        }
    }

    private static LinkedHashMap<Integer, ReleaseLabel> checkFrequency(String sql) throws Exception {
        // TODO Auto-generated method stub
        Connection con = ConnectionPool.getConnection();
        LinkedHashMap<Integer, ReleaseLabel> records = new LinkedHashMap<Integer, ReleaseLabel>();

        if (!con.isClosed()) {
            int i = 0;
            Statement st = con.createStatement();
            System.out.println(sql);
            ResultSet result = st.executeQuery(sql);

            int current_cluster = -1;
            int previous_year = 0;
            String previous_release = "";

            //select id, release_name, genres, styles, country, date, artist_name, label_name, catalog_num, format, format_qty, format_description, extra_artist, cluster_id
            // 		 1, 	2,			3, 		4, 		5, 		6, 		7, 				8, 			9		10,			11				12				13			14
            while (result.next()) {
                int id = result.getInt(1);
                String release = result.getString(2);
                String genres = result.getString(3);
                String styles = result.getString(4);
                String country = result.getString(5);
                String year = result.getString(6);
                //System.out.println(year);
                year = year.substring(0, 4);
                String artist = result.getString(7);
                String label = result.getString(8);
                String catno = result.getString(9);
                String format = result.getString(10);
                String qty = result.getString(11);
                String description = result.getString(12);
                String extra_artist = result.getString(13);
                int cluster_id = result.getInt(14);
                //String[] split = year2.split("-");


                //System.out.println(i+"\t"+id+"\t"+release+"\t"+label+"\t"+country+"\t"+year+"\t"+genres+"\t"+styles+"\t"+artist+"\t"+extra_artist+"\t"+catno+"\t"+format+"\t"+qty+"\t"+description+"\t"+cluster_id);


                if (current_cluster != cluster_id) {
                    current_cluster = cluster_id;
                    previous_year = Integer.parseInt(year);
                    previous_release = release;
                } else {
                    int distance = Math.abs(Integer.parseInt(year) - previous_year);
                    if (distance > 10)
                        //System.out.println("\t\t"+previous_year+"\t"+year+"\t"+distance+"\t"+label+"\t"+release+"\t"+previous_release+"\t"+cluster_id);
                        previous_year = Integer.parseInt(year);
                    previous_release = release;
                }

                if (records.containsKey(id)) {
                    ReleaseLabel releaselabel = records.get(id);
                    ArrayList artists = releaselabel.getArtist();
                    if (!artists.contains(artist))
                        artists.add(artist);
                    ArrayList extra_artists = releaselabel.getExtra_artist();
                    if (!extra_artists.contains(extra_artist))
                        extra_artists.add(extra_artist);
                } else {
                    ReleaseLabel releaselabel = new ReleaseLabel();
                    ArrayList artists = new ArrayList();
                    artists.add(artist);
                    releaselabel.setArtist(artists);
                    releaselabel.setCatno(catno);
                    releaselabel.setCluster_id(cluster_id);
                    releaselabel.setCountry(country);
                    releaselabel.setDate(Integer.parseInt(year));
                    ArrayList extra_artists = new ArrayList();
                    extra_artists.add(extra_artist);
                    releaselabel.setExtra_artist(extra_artists);
                    releaselabel.setFormat(format);
                    releaselabel.setFormat_description(description);
                    releaselabel.setGenres(genres);
                    releaselabel.setId(id);
                    releaselabel.setLabel(label);
                    releaselabel.setQty(qty);
                    releaselabel.setRelease(release);
                    releaselabel.setStyles(styles);

                    records.put(id, releaselabel);

                }


                i++;

            }
            System.out.println(i);
            st.close();
        }

        ConnectionPool.putConnection(con);
        return records;
    }

    private static void checkConsistency(String sql) throws Exception {
        // TODO Auto-generated method stub
        Connection con = ConnectionPool.getConnection();

        if (!con.isClosed()) {
            int i = 0;
            Statement st = con.createStatement();
            System.out.println(sql);
            ResultSet result = st.executeQuery(sql);

            //select id, release_name, country, year, label_name, discogs_name, discogs_country, discogs_date, discogs_label
            //        1, 	2,			3, 		4, 		5, 			6, 				7, 				8, 				9
            while (result.next()) {
                int id = result.getInt(1);
                String release1 = result.getString(2);
                String country1 = result.getString(3);
                //country = AbstractBlock.cleanValue(country);
                int year1 = result.getInt(4);
                String label1 = result.getString(5);
                String release2 = result.getString(6);
                String country2 = result.getString(7);
                String year2 = result.getString(8);
                String label2 = result.getString(9);
                //String[] split = year2.split("-");


                if (year1 != 0 && !year2.equals("") && !year2.equals("?") && !year2.equals("None")) {
                    year2 = year2.substring(0, 4);
                    int distance = Math.abs(year1 - Integer.parseInt(year2));
                    if (distance > 0 && label1.equals(label2)) {
                        System.out.println(i + "\t" + id + "\t" + release1 + "\t" + label1 + "\t" + country1 + "\t" + year1 + "\t" + release2 + "\t" + label2 + "\t" + country2 + "\t" + year2 + "\t" + distance);
                        i++;
                    }

                }

            }

            st.close();
        }

        ConnectionPool.putConnection(con);
    }

}
