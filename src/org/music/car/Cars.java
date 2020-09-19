package org.music.car;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.music.connection.ConnectionPool;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

public class Cars {

    public void processCarLink(String html) throws IOException {
        Document doc = Jsoup.connect(html).timeout(10 * 1000).get();
        Elements meta = doc.select("a[href]");

        for (Element e : meta) {
            if (e.attr("href").startsWith("/en/car/porsche/"))
                System.out.println(e.attr("href"));
        }
    }

    public void processCars(ArrayList<Link> links) throws Exception {
        Random random = new Random();
        Connection con = ConnectionPool.getConnection();

        if (!con.isClosed()) {
            Statement st = con.createStatement();
            int j = 0;
            for (int i = 0; i < links.size(); i++) {
                Link link = links.get(i);
                int millis = Math.abs(random.nextInt(50));
                System.out.println(millis);
                Thread.sleep(millis);
                System.out.println(j + "\t" + link.url);

                Car car = new Car().parseCar(link.getUrl());
                String brand = link.getBrand();
                if (brand.equals(""))
                    brand = link.getUrl().replace("https://www.classicdriver.com/en/car/", "").split("/")[0];
                car.setBrand(brand);
                car.setLink(link.getUrl());
                car.setModel(link.getModel());

                insertCar(car, st);
                j++;
            }
            st.close();
        }

        ConnectionPool.putConnection(con);
    }

    public ArrayList<Car> getCars(ArrayList<String> links) throws InterruptedException, IOException {
        Random random = new Random();
        ArrayList<Car> cars = new ArrayList<Car>();

        for (int i = 0; i < links.size(); i++) {
            String link = links.get(i);
            int millis = Math.abs(random.nextInt(100) * 500);
            System.out.println(millis);
            Thread.sleep(millis);
            System.out.println(millis + "\t" + link);

            Car car = new Car().parseCar(link);
            String brand = link.replace("https://www.classicdriver.com/en/car/", "").split("/")[0];
            car.setBrand(brand);

            cars.add(cars.size(), car);
        }

        return cars;
    }

    public void insertCar(Car car, Statement st) throws Exception {
        int i = 0;

        String vin = car.getVin();
        String year = car.getYear();
        String carType = car.getCarType();
        String title = car.getTitle();
        String description = car.getDescription();
        String model = car.getModel();
        String brand = car.getBrand();
        String link = car.getLink();

        String sql = "INSERT INTO car.car (vin, year, cartype, title, description, model, brand, link) VALUES ('" + vin
                + "','" + year + "','" + carType + "','" + title + "','" + description + "','" + model + "','" + brand
                + "','" + link + "')";

        System.out.println(sql);
        st.execute(sql);
    }

    public void insertCars(ArrayList<Car> cars) throws Exception {
        Connection con = ConnectionPool.getConnection();

        if (!con.isClosed()) {
            int i = 0;
            Statement st = con.createStatement();

            for (Car car : cars) {
                String vin = car.getVin();
                String year = car.getYear();
                String carType = car.getCarType();
                String title = car.getTitle();
                String description = car.getDescription();
                String model = car.getModel();
                String brand = car.getBrand();

                String sql = "INSERT INTO car.car (vin, year, cartype, title, description, model, brand) VALUES ('"
                        + vin + "','" + year + "','" + carType + "','" + title + "','" + description + "','" + model
                        + "','" + brand + "')";

                System.out.println(sql);
                st.execute(sql);
            }

            st.close();
        }
        ConnectionPool.putConnection(con);
    }
}
