import models.Command;
import models.Simulation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

class Main {

    public static void main(String[] args) throws IOException {
        if ( args.length < 1 ) {
            System.out.println("Need input file");
            return;
        }

        final Path inputFile = Paths.get(args[0]);

        final List<String> lines = Files.readAllLines(inputFile);

        Parser p = new Parser();
        Simulation simulation = p.parse(lines);
        System.out.println(simulation.toString());

        final List<Command> commands = simulation.simulate();

        final List<String> cmdStrings = commands.stream()
                .map(Command::format)
                .collect(Collectors.toList());
        cmdStrings.add(0, Integer.toString(commands.size()));

        cmdStrings.forEach(System.out::println);

        final Path outputFile = Paths.get(inputFile + ".sim");
        Files.write(outputFile, cmdStrings, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }
}
