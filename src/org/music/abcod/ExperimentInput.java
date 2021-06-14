package org.music.abcod;

public class ExperimentInput {
    String musicAB = "select id, date, title as release_1, catno, label as label_name from music.music_release " +
            "where (date NOT IN (' ', ' ', ' ', ' ') AND date IS NOT NULL) order by label, catno, date";

    String musicABCForScalability = "select concat(label, regexp_replace(catno, '[0-9]*', '', 'g'))  as group_id, date as time from music.music_release " +
            "where (date NOT IN (' ', ' ', ' ', ' ') AND date IS NOT NULL) " +//AND label = 'A&M Records'
            "order by group_id, catno, date";

    String sfoFlightAB = "select time_in_hr as time from music.sfo_flight order by airline, flight_number, transaction, date";
    String sfoFlightABCForScalability = "select concat(airline, flight_number, transaction) as group_id, time_in_hr as time from music.sfo_flight order by group_id, date, time";
    String sfoFlightForBandWidth = "select concat(airline, flight_number, transaction) as group_id, time_in_hr as time from music.sfo_flight " +
            " where flight_number='JL2' and transaction ='ARR'  order by group_id, date, time";//EI146
    String sfoFlightABCForMultipleAttributes = "select concat(airline, flight_number, transaction) as group_id, hr, minute from music.sfo_flight order by group_id, date, time";


    String usFlightAB = "select dep_time_in_hr as time from music.nationwide_2018_flight order by origin_airport_id, fl_date, crs_dep_time_in_hr, op_carrier_fl_num ";
    String usFlightForBandWidth = "select concat(origin, fl_date) as group_id, dep_time_in_hr as time from music.nationwide_2018_flight " +
            "where origin = 'ABE' and fl_date = '2018-08-06' " +
            "order by group_id, crs_dep_time_in_hr, op_carrier_fl_num, origin_airport_id, op_carrier_airline_id,  dep_time_in_hr";
    String usFlightForScalability = "select concat(origin, fl_date) as group_id, dep_time_in_hr as time from music.nationwide_2018_flight " +
            "order by group_id, crs_dep_time_in_hr, op_carrier_fl_num, origin_airport_id, op_carrier_airline_id,  dep_time_in_hr";
    String usFlightForMultiAttributes = "select concat(origin, fl_date) as group_id, dep_time_in_hr as hr, 'us' as minute from music.nationwide_2018_flight where dep_time_in_hr is not null " +
            "order by group_id, crs_dep_time_in_hr, op_carrier_fl_num, origin_airport_id, op_carrier_airline_id,  dep_time_in_hr";

    String softwareBugForBandWidth = "select 'CDT' as group_id, assigned as time from music.software_bug order by new, assigned";
    String softwareBugForScalability = "select 'CDT' as group_id, assigned as time from music.software_bug order by new, assigned";
}
