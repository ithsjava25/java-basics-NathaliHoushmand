package com.example;

import com.example.api.ElpriserAPI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


//
//Commit #1
// CLI och bas fixat
public class Main {
    static class PricePoint{
        LocalDateTime time;
        double price;

            PricePoint(LocalDateTime time, double price) {
                this.time = time;
                this.price = price;
            }

            String getHourString() {
                int hour = time.getHour();
                int next = (hour + 1) % 24;
                return String.format("%02d-%02d", hour, next);
            }

            double price() {
                return price;
            }
    }
    public static void main(String[] args) {
        ElpriserAPI elpriserAPI = new ElpriserAPI();

        String zone = "SE3";
        LocalDate date = LocalDate.now();
        int chargingHours = 0;
        boolean showHelp = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--zone":
                    if (i + 1 < args.length) zone = args [++i];
                    break;
                case "--date":
                    if (i + 1 <args.length) date =LocalDate.parse(args[++i]);
                    break;
                case "--charging":
                    if (i + 1 < args.length){
                        String ch = args[++i];
                        if (ch.equals("2h")) chargingHours = 2;
                        else if (ch.equals("4h")) chargingHours = 4;
                        else if (ch.equals("8h")) chargingHours = 8;
                    }
                    break;
                case "--help":
                    showHelp = true;
                    break;
            }
            
        }

        if (showHelp || zone == null){
            printHelp();
            return;
        }
//


        //Commit #3 - Hämta och analysera priser från API
        //---
        
        List<PricePoint> prices = getPrices(zone, date, elpriserAPI);

        if (prices == null || prices.isEmpty()) {
            System.out.println("No price data available for " + date + " in zone" + zone);
            return;
        }
        //

        //Commit #4 - visa priser mean, min och max timmar
        for (PricePoint p : prices) {
            System.out.printf("%s %.2f öre%n", p.getHourString(), p.price());
        }

        double mean = prices.stream().mapToDouble(PricePoint::price).average().orElse(0);
        System.out.printf("Medelpris: %.2f öre%n", mean);

        PricePoint min = prices.stream().min(Comparator.comparingDouble(PricePoint::price)).get();
        PricePoint max = prices.stream().max(Comparator.comparingDouble(PricePoint::price)).get();

        System.out.printf("Billigast timme: %s %.2f öre%n", min.getHourString(), min.price());
        System.out.printf("Dyrast timme: %s %.2f öre%n", max.getHourString(), max.price());

        //Commit#5 - charging window tillagd
        if (chargingHours > 0) {
            double lowestSum = Double.MAX_VALUE;
            int bestStart = 0;

            for (int i = 0; i <= prices.size() - chargingHours; i++) {
                double sum = 0;
                for (int j = i; j < i + chargingHours; j++) {
                    sum += prices.get(j).price();                    
                }
                if (sum< lowestSum){
                    lowestSum = sum;
                    bestStart = i;
                }
        }

        System.out.println("Optimalt laddfönster (" + chargingHours + "h):");
        for (int k = bestStart; k < bestStart + chargingHours; k++) {
            PricePoint p = prices.get(k);
            System.out.printf("%s %.2f öre%n", p.getHourString(), p.price());
        }

    }



    }

    private static List<PricePoint> getPrices(String zone, LocalDate date, ElpriserAPI api) {
        ElpriserAPI.Prisklass prisklass = ElpriserAPI.Prisklass.valueOf(zone);

        List<ElpriserAPI.Elpris> apiPrices = api.getPriser(date, prisklass);

        List<PricePoint> prices = new ArrayList<>();
        for (var elpris : apiPrices) {
            prices.add(new PricePoint(elpris.timeStart().toLocalDateTime(), elpris.sekPerKWh()));

        }

        return prices;
        
    }



    //Commit #3 - printHelp metod implementerad då det inte funkar ovan utan
     
    private static void printHelp() {
    System.out.println("Usage: java -cp target/classes com.example.Main --zone SE1|SE2|SE3|SE4 [options]");
    System.out.println("--date YYYY-MM-DD   Specify date (default today)");
    System.out.println("--sorted            Display prices sorted by value");
    System.out.println("--charging 2h|4h|8h Find optimal charging window");
    System.out.println("--help              Show this help");
}
    //

}
