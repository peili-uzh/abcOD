package org.music.car;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class WebCrawler {

    public static void main(String[] args) throws Exception {

        /**
         * Import links
         */
        new Links().importLink();

        /**
         * Parse car
         *
         * // String sql = "Select distinct url, '' as brand from car.link order
         * by // url limit 50 offset 950";
         **/
        String brandSql = "select url, brand, model, year from car.brand_link where id = 6281 and "
                + "url <> 'https://www.classicdriver.com/en/car/mercedes-benz/sl/1996/412120' and "
                + "url <> 'https://www.classicdriver.com/en/car/jaguar/e-type-si/1961/412129' offset 1270";
        ArrayList<Link> links = new Links().getLinks(brandSql);
        // new Cars().processCars(links);

        /**
         * crawl links
         *
         */
        String urls = "https://www.classicdriver.com/en/car/bentley/8-litre/1935/412970,https://www.classicdriver.com/en/car/taydec/mk3/1970/414458,https://www.classicdriver.com/en/car/rolls-royce/corniche-i/1984/414461,https://www.classicdriver.com/en/car/rolls-royce/corniche-i/1973/414464,https://www.classicdriver.com/en/car/daimler/v8/2001/414459,https://www.classicdriver.com/en/car/rolls-royce/silver-wraith-ii/1980/414463,https://www.classicdriver.com/en/car/audi/80/1994/414467,https://www.classicdriver.com/en/car/bentley/continental-gt-speed/2016/414375,https://www.classicdriver.com/en/car/ac/cobra/1994/414360,https://www.classicdriver.com/en/car/bristol/411/1975/414339,https://www.classicdriver.com/en/car/alfa-romeo/tz1/1965/414337,https://www.classicdriver.com/en/car/packard/custom-eight/1948/414328,https://www.classicdriver.com/en/car/lincoln/continental/1965/414324,https://www.classicdriver.com/en/car/cadillac/deville/1967/414261,https://www.classicdriver.com/en/car/ford/sierra-cosworth/1991/414273,https://www.classicdriver.com/en/car/jaguar/daimler/1991/414278,https://www.classicdriver.com/en/car/jaguar/s-type/1965/414279,https://www.classicdriver.com/en/car/jaguar/daimler/1982/414280,https://www.classicdriver.com/en/car/peugeot/406/1999/414302,https://www.classicdriver.com/en/car/triumph/tr3/1958/414313,https://www.classicdriver.com/en/car/volvo/121/1967/414314,https://www.classicdriver.com/en/car/seat/600/1973/414311,https://www.classicdriver.com/en/car/triumph/tr6/1976/414312,https://www.classicdriver.com/en/car/morris/minor/1954/414255,https://www.classicdriver.com/en/car/bmw/528/1980/414257,https://www.classicdriver.com/en/car/abarth/1000/1962/414243,https://www.classicdriver.com/en/car/abarth/695/2001/413582,https://www.classicdriver.com/en/car/abarth/695-ss/1964/408166,https://www.classicdriver.com/en/car/abarth/850/1960/404883,https://www.classicdriver.com/en/car/abarth/1000-gt/1963/403729,https://www.classicdriver.com/en/car/abarth/695/2011/402653,https://www.classicdriver.com/en/car/abarth/695/2015/398154,https://www.classicdriver.com/en/car/abarth/595/2016/397185,https://www.classicdriver.com/en/car/abarth/595/2016/397186,https://www.classicdriver.com/en/car/abarth/formula-italia/1972/397103,https://www.classicdriver.com/en/car/abarth/se10/1970/389931,https://www.classicdriver.com/en/car/abarth/1000/1963/382518,https://www.classicdriver.com/en/car/abarth/750/1958/352480,https://www.classicdriver.com/en/car/abarth/simca/1963/309247,https://www.classicdriver.com/en/car/abarth/600/1973/299030";
        // "https://www.classicdriver.com/en/car/rolls-royce/camargue/1976/414482,https://www.classicdriver.com/en/car/bentley/continental-convertible/1953/401043,https://www.classicdriver.com/en/car/bentley/8-litre/1935/412970,https://www.classicdriver.com/en/car/porsche/911/1973/414481,https://www.classicdriver.com/en/car/porsche/911-g/1973/414478,https://www.classicdriver.com/en/car/chevrolet/corvette/2001/359000,https://www.classicdriver.com/en/car/jaguar/c-type/1967/414477,https://www.classicdriver.com/en/car/lamborghini/jarama/1972/337116,https://www.classicdriver.com/en/car/porsche/911-964-carrera/1991/414363,https://www.classicdriver.com/en/car/taydec/mk3/1970/414458,https://www.classicdriver.com/en/car/mini-classic/cooper/1970/414468,https://www.classicdriver.com/en/car/bentley/t-i/1976/414469,https://www.classicdriver.com/en/car/rolls-royce/corniche-i/1984/414461,https://www.classicdriver.com/en/car/rolls-royce/phantom/2005/414462,https://www.classicdriver.com/en/car/rolls-royce/corniche-i/1973/414464,https://www.classicdriver.com/en/car/fiat/500/1971/414466,https://www.classicdriver.com/en/car/mercedes-benz/s-class/1988/414470,https://www.classicdriver.com/en/car/bentley/mulsanne/2012/414460,https://www.classicdriver.com/en/car/daimler/v8/2001/414459,https://www.classicdriver.com/en/car/rolls-royce/silver-wraith-ii/1980/414463,https://www.classicdriver.com/en/car/audi/80/1994/414467,https://www.classicdriver.com/en/car/austin-healey/3000/1963/414471,https://www.classicdriver.com/en/car/ford/capri/1972/414472,https://www.classicdriver.com/en/car/aston-martin/v8-vantage/2006/414376,https://www.classicdriver.com/en/car/bentley/continental-gt-speed/2016/414375,https://www.classicdriver.com/en/car/chevrolet/corvette/1961/414374,https://www.classicdriver.com/en/car/aston-martin/v8/1974/414373,https://www.classicdriver.com/en/car/jaguar/xk-120/1950/414372,https://www.classicdriver.com/en/car/porsche/911-993-carrera/1995/414367,https://www.classicdriver.com/en/car/alfa-romeo/giulietta/1956/414359,https://www.classicdriver.com/en/car/alfa-romeo/2000/1972/414361,https://www.classicdriver.com/en/car/ac/cobra/1994/414360,https://www.classicdriver.com/en/car/bugatti/veyron/2008/414356,https://www.classicdriver.com/en/car/ferrari/575/2004/414354,https://www.classicdriver.com/en/car/vw/beetle/1961/414347,https://www.classicdriver.com/en/car/mercedes-benz/sl-pagode/1963/414345,https://www.classicdriver.com/en/car/lola/t70/1968/414343,https://www.classicdriver.com/en/car/bristol/411/1975/414339,https://www.classicdriver.com/en/car/aston-martin/db24/1953/334105,https://www.classicdriver.com/en/car/alfa-romeo/tz1/1965/414337,https://www.classicdriver.com/en/car/land-rover/range-rover/1994/371841,https://www.classicdriver.com/en/car/porsche/911-turbo/1989/341971,https://www.classicdriver.com/en/car/packard/custom-eight/1948/414328,https://www.classicdriver.com/en/car/porsche/911-gt3/2005/394097,https://www.classicdriver.com/en/car/vw/beetle/1979/414322,https://www.classicdriver.com/en/car/lincoln/continental/1965/414324,https://www.classicdriver.com/en/car/alfa-romeo/giulia/1973/414254,https://www.classicdriver.com/en/car/bmw/635-csi/1987/414258,https://www.classicdriver.com/en/car/cadillac/deville/1967/414261,https://www.classicdriver.com/en/car/fiat/500/1952/414267,https://www.classicdriver.com/en/car/fiat/500/1949/414268,https://www.classicdriver.com/en/car/ford/thunderbird/1961/414271,https://www.classicdriver.com/en/car/ford/thunderbird/1957/414272,https://www.classicdriver.com/en/car/ford/sierra-cosworth/1991/414273,https://www.classicdriver.com/en/car/jaguar/daimler/1991/414278,https://www.classicdriver.com/en/car/jaguar/s-type/1965/414279,https://www.classicdriver.com/en/car/jaguar/daimler/1982/414280,https://www.classicdriver.com/en/car/mercedes-benz/sl/1973/414283,https://www.classicdriver.com/en/car/mercedes-benz/190-e/1987/414284,https://www.classicdriver.com/en/car/mercedes-benz/w111112/1963/414289,https://www.classicdriver.com/en/car/mg/b/1963/414292,https://www.classicdriver.com/en/car/mg/a/1958/414294,https://www.classicdriver.com/en/car/mg/b/1968/414298,https://www.classicdriver.com/en/car/morgan/44/1972/414299,https://www.classicdriver.com/en/car/peugeot/406/1999/414302,https://www.classicdriver.com/en/car/porsche/911-g/1978/414308,https://www.classicdriver.com/en/car/triumph/tr3/1958/414313,https://www.classicdriver.com/en/car/volvo/121/1967/414314,https://www.classicdriver.com/en/car/volvo/p1800/1969/414320,https://www.classicdriver.com/en/car/vw/beetle/1971/414323,https://www.classicdriver.com/en/car/seat/600/1973/414311,https://www.classicdriver.com/en/car/triumph/tr6/1976/414312,https://www.classicdriver.com/en/car/morris/minor/1954/414255,https://www.classicdriver.com/en/car/bmw/528/1980/414257,https://www.classicdriver.com/en/car/abarth/1000/1962/414243,https://www.classicdriver.com/en/car/abarth/695/2001/413582,https://www.classicdriver.com/en/car/abarth/695-ss/1964/408166,https://www.classicdriver.com/en/car/abarth/850/1960/404883,https://www.classicdriver.com/en/car/abarth/850/1960/404883,https://www.classicdriver.com/en/car/abarth/1000-gt/1963/403729,https://www.classicdriver.com/en/car/abarth/695/2011/402653,https://www.classicdriver.com/en/car/abarth/695/2015/398154,https://www.classicdriver.com/en/car/abarth/595/2016/397185,https://www.classicdriver.com/en/car/abarth/595/2016/397186,https://www.classicdriver.com/en/car/abarth/formula-italia/1972/397103,https://www.classicdriver.com/en/car/abarth/se10/1970/389931,https://www.classicdriver.com/en/car/abarth/1000/1963/382518,https://www.classicdriver.com/en/car/abarth/750/1958/352480,https://www.classicdriver.com/en/car/abarth/simca/1963/309247,https://www.classicdriver.com/en/car/abarth/600/1973/299030";

        String[] urlsplit = urls.split(",");
        ArrayList pages = new ArrayList();

        for (int i = 0; i < urlsplit.length; i++) {
            String url = urlsplit[i];
            parsePage(url, pages);
        }

        String sql = "Select url, brand from car.brand_link order by id limit 1 offset 1932";
        // ArrayList<Link> links = new Links().getLinks(sql); // ArrayList
        pages = new ArrayList();
        for (Link link : links) { //
            parsePage(link.url, pages);
        }

    }

    public static void parsePage(String url, ArrayList<String> pages) throws IOException, InterruptedException {
        if (!pages.contains(url)) {
            pages.add(url);
        }

        // if (url.startsWith("/en/car/"))
        Document doc = (Document) Jsoup.connect(url).timeout(20 * 1000).get();
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if (href.startsWith("/en/car/")) {
                href = "https://www.classicdriver.com" + href;
                if (!pages.contains(href)) {
                    System.out.println(href);
                    Random random = new Random();
                    int millis = Math.abs(random.nextInt(50));
                    // System.out.println(millis);
                    Thread.sleep(millis);
                    parsePage(href, pages);

                }
                /**
                 * if (!pages.contains(href)) { pages.add(href); } if
                 * (pages.contains(url)) { pages.remove(url); }
                 */
            }
        }

        // for (String page : pages) {
        // parsePage(page, pages);
        // System.out.println(pages.get(i));
        // }
    }
}