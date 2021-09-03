package org.music.abod;

public class ExperimentInput {
    String musicForABScalability = "select date as time from music.music_release " +
            "where (date NOT IN (' ', ' ', ' ', ' ') AND date IS NOT NULL) " +
            "order by label, catno, date";
    String SFOForScalability = "select  time_in_hr as time from music.sfo_flight where time_in_hr IS NOT NULL and date IS NOT NULL order by airline, flight_number, transaction, date";
    // sfo flight
//                "select time_in_hr as time from music.sfo_flight " +
//                        "where time_in_hr IS NOT NULL and date IS NOT NULL " +
//                        "order by airline, flight_number, transaction, date";

    String USForScalability =
            "select dep_time_in_hr as time from music.nationwide_2018_flight order by origin_airport_id, fl_date, crs_dep_time_in_hr, op_carrier_fl_num ";
//            "select dep_time_in_hr as time from music.nationwide_2018_flight " +
//            "order by concat(origin, fl_date), crs_dep_time_in_hr, op_carrier_fl_num, origin_airport_id, op_carrier_airline_id,  dep_time_in_hr";

    //// music accuracy test:
    String musicAccuracy = "select id, date, title as release_1, catno, label as label_name from music.music_release " +
            "where (date NOT IN (' ', ' ', ' ', ' ') AND date IS NOT NULL) " +
            "order by label, catno, date";
}
