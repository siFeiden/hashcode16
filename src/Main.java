//import models.Drone;
import models.Simulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if ( args.length < 1 ) {
            System.out.println("Need input file");
            return;
        }

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parser p = new Parser();
        Simulation sim = p.parse(lines);
        System.out.println(sim.toString());

        //simulate(sim);
    }
}
