package com.example;

import com.example.api.ElpriserAPI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;



public class Main {
    public static void main(String[] args) {
        ElpriserAPI elpriserAPI = new ElpriserAPI();

        String zone = null;
        LocalDate date = LocalDate.now();
        boolean sorted = false;
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
                case "--sorted":
                    sorted = true;
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










































    }

    private static void printHelp() {
    System.out.println("Usage: java -cp target/classes com.example.Main --zone SE1|SE2|SE3|SE4 [options]");
    System.out.println("--date YYYY-MM-DD   Specify date (default today)");
    System.out.println("--sorted            Display prices sorted by value");
    System.out.println("--charging 2h|4h|8h Find optimal charging window");
    System.out.println("--help              Show this help");
}

}
