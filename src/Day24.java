import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day24 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input24.txt"));

        part01(input);
        part02(input);
    }

    private static void part01(List<String> input) {
        var colored = parseInput(input);
        System.out.println(colored.size());
    }

    private static void part02(List<String> input) {
        var colored = parseInput(input);

        for (int i = 0; i < 100; i++) {
            colored = gameOfLifeStep(colored);
        }
        System.out.println(colored.size());
    }

    private static Set<HexCoordinate> parseInput(List<String> input) {
        var colored = new HashSet<HexCoordinate>();
        input.stream()
            .map(HexCoordinate::parse)
            .forEach(coordinate -> {
                if (colored.contains(coordinate)) {
                    colored.remove(coordinate);
                } else {
                    colored.add(coordinate);
                }
            });
        return colored;
    }

    private static Set<HexCoordinate> gameOfLifeStep(Set<HexCoordinate> colored) {
        return colored.parallelStream()
            .map(HexCoordinate::neighboursAndSelf)
            .flatMap(Collection::stream)
            .distinct()
            .map(coordinate -> {
                var neighbourCount = coordinate.neighbours().parallelStream().filter(colored::contains).count();
                if (colored.contains(coordinate)) {
                    if (neighbourCount == 0 || neighbourCount > 2) {
                        return null;
                    }
                    return coordinate;
                } else if (neighbourCount == 2) {
                    return coordinate;
                } else {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    record HexCoordinate(int x, int y, int z) {
        List<HexCoordinate> neighbours() {
            var result = new ArrayList<HexCoordinate>();
            result.add(new HexCoordinate(x, y + 1, z - 1));
            result.add(new HexCoordinate(x + 1, y, z - 1));
            result.add(new HexCoordinate(x + 1, y - 1, z));
            result.add(new HexCoordinate(x, y - 1, z + 1));
            result.add(new HexCoordinate(x - 1, y, z + 1));
            result.add(new HexCoordinate(x - 1, y + 1, z));
            return result;
        }

        List<HexCoordinate> neighboursAndSelf() {
            var result = neighbours();
            result.add(new HexCoordinate(x, y, z));
            return result;
        }

        static HexCoordinate parse(String line) {
            var scanner = new Scanner(line);
            scanner.useDelimiter("");

            var x = 0;
            var y = 0;
            var z = 0;
            while (scanner.hasNext()) {
                var coordinate = scanner.next();
                if (coordinate.equals("s") || coordinate.equals("n")) {
                    coordinate += scanner.next();
                }

                switch (coordinate) {
                    case "e" -> {
                        x++;
                        y--;
                    }
                    case "w" -> {
                        x--;
                        y++;
                    }
                    case "ne" -> {
                        x++;
                        z--;
                    }
                    case "nw" -> {
                        y++;
                        z--;
                    }
                    case "se" -> {
                        z++;
                        y--;
                    }
                    case "sw" -> {
                        x--;
                        z++;
                    }
                }
            }
            return new HexCoordinate(x, y, z);
        }
    }
}
