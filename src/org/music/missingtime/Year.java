// package org.music.missingtime;
//
// public class Year {
//
// /*
// * Year as one attribute
// */
// public Integer calenderYear;
//
// /*
// * Year as two attributes:
// */
// public Integer centuries;
// public Integer decades;
//
// /*
// * Year as four attributes:
// */
// public Integer millennium;
// public Integer century;
// public Integer decade;
// public Integer year;
// public Integer month;
//
// /*
// * constructor
// */
//
// public void printYear() {
// System.out.println(this.calenderYear);
// System.out.println(this.centuries);
// System.out.println(this.decades);
//
// System.out.println(this.millennium);
// System.out.println(this.century);
// System.out.println(this.decade);
// System.out.println(this.year);
// System.out.println(this.month);
//
// System.out.println();
// }
//
// public Year(Integer yearValue) throws Exception {
// this.calenderYear = yearValue;
//
// String stringValue = yearValue.toString();
// if (stringValue.length() == 4) {
// this.centuries = Integer.parseInt(stringValue.substring(0, 2));
// this.decades = Integer.parseInt(stringValue.substring(2));
//
// this.millennium = Integer.parseInt(stringValue.substring(0, 1));
// this.century = Integer.parseInt(stringValue.substring(1, 2));
// this.decade = Integer.parseInt(stringValue.substring(2, 3));
// this.year = Integer.parseInt(stringValue.substring(3));
// this.month = 0;
// } else {
// if (stringValue.length() == 6) {
// this.centuries = Integer.parseInt(stringValue.substring(0, 2));
// this.decades = Integer.parseInt(stringValue.substring(2));
//
// this.millennium = Integer.parseInt(stringValue.substring(0, 1));
// this.century = Integer.parseInt(stringValue.substring(1, 2));
// this.decade = Integer.parseInt(stringValue.substring(2, 3));
// this.year = Integer.parseInt(stringValue.substring(3));
// this.month = Integer.parseInt(stringValue.substring(4)) * 10
// + Integer.parseInt(stringValue.substring(5));
// }
// if (stringValue.length() != 4 && stringValue.length() != 6 && yearValue != 0)
// {
// throw new Exception("Unexpected year: " + yearValue);
// }
// }
//
// }
//
// public Integer getCalenderYear() {
// return calenderYear;
// }
//
// public void setCalenderYear(Integer calenderYear) {
// this.calenderYear = calenderYear;
// }
//
// public Integer getCenturies() {
// return centuries;
// }
//
// public void setCenturies(Integer centuries) {
// this.centuries = centuries;
// }
//
// public Integer getDecades() {
// return decades;
// }
//
// public void setDecades(Integer decades) {
// this.decades = decades;
// }
//
// public Integer getMillennium() {
// return millennium;
// }
//
// public void setMillennium(Integer millennium) {
// this.millennium = millennium;
// }
//
// public Integer getCentury() {
// return century;
// }
//
// public void setCentury(Integer century) {
// this.century = century;
// }
//
// public Integer getDecade() {
// return decade;
// }
//
// public void setDecade(Integer decade) {
// this.decade = decade;
// }
//
// public Integer getYear() {
// return year;
// }
//
// public void setYear(Integer year) {
// this.year = year;
// }
// }
