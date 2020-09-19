package org.music.car;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Car {
    String vin;
    String year;
    String carType;
    String title;
    String description;
    String model;
    String brand;
    String link;

    public Car parseCar(String html) throws IOException {

        Document doc = Jsoup.connect(html.replace("(", "").replace(")", "")).timeout(10 * 1000).get();
        Car car = new Car();

        Element title = doc.select("title").first();
        String titleString = title.text().split(" - ")[0].replace("'", "");

        String descriptionString = "";
        Elements meta = doc.select("meta[name]");
        for (Element e : meta) {
            if (e.attr("name").equals("description")) {
                descriptionString = e.attr("content");
            }
        }

        String yearString = "";
        String vinString = "";
        String typeString = "";
        ArrayList featureName = new ArrayList();
        Elements contents = doc.select("div[class]");
        Iterator<Element> iterator = contents.iterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            if (element.attr("class").equals("field-label") || element.attr("class").equals("field-items")) {
                String text = element.text();
                if (!text.equals("")) {
                    featureName.add(featureName.size(), text);
                }
            }
        }

        Integer yearIndex = featureName.indexOf("Year of manufacture ");
        yearString = "";
        if (yearIndex > -1) {
            yearString = featureName.get(yearIndex + 1).toString();
        }
        Integer typeIndex = featureName.indexOf("Car type ");
        typeString = "";
        if (typeIndex > -1)
            typeString = featureName.get(typeIndex + 1).toString();
        Integer vinIndex = featureName.indexOf("Chassis number ");
        if (vinIndex > -1) {
            vinString = featureName.get(vinIndex + 1).toString();
        }

        descriptionString = descriptionString.replace("'", "").replace("\"", "");
        int upperBound = Math.min(500, descriptionString.length());

        car.setTitle(titleString);
        car.setDescription(descriptionString.substring(0, upperBound));
        car.setCarType(typeString);
        car.setVin(vinString);
        car.setYear(yearString);

        System.out.println(car.title + ";\t" + car.carType + ";\t" + car.year + ";\t" + car.vin + "\t.");

        return car;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
