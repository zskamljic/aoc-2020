import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Day12 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input12.txt"));
        var list = input.stream()
            .map(Instruction::parse)
            .collect(Collectors.toList());

        part01(list);
        part02(list);
    }

    private static void part01(List<Instruction> instructions) {
        var ship = new Ship();
        instructions.forEach(ship::apply);
        System.out.println(ship.manhattanDistance());
    }

    private static void part02(List<Instruction> list) {
        int x = 0;
        int y = 0;
        var waypoint = new Point(10, -1);

        for (var instruction : list) {
            switch (instruction.direction) {
                case 'N' -> waypoint.y -= instruction.count;
                case 'S' -> waypoint.y += instruction.count;
                case 'E' -> waypoint.x += instruction.count;
                case 'W' -> waypoint.x -= instruction.count;
                case 'L' -> waypoint.rotate(-instruction.count);
                case 'R' -> waypoint.rotate(instruction.count);
                case 'F' -> {
                    x += instruction.count * waypoint.x;
                    y += instruction.count * waypoint.y;
                }
            }
        }
        System.out.println(Math.abs(x) + Math.abs(y));
    }

    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void rotate(int degrees) {
            var count = ((degrees + 360) / 90) % 4;
            switch (count) {
                case 1 -> {
                    var newX = -y;
                    //noinspection SuspiciousNameCombination
                    y = x;
                    x = newX;
                }
                case 2 -> {
                    x = -x;
                    y = -y;
                }
                case 3 -> {
                    //noinspection SuspiciousNameCombination
                    var newX = y;
                    y = -x;
                    x = newX;
                }
            }
        }
    }

    static class Ship {
        private static final int NORTH = 0;
        private static final int EAST = 1;
        private static final int SOUTH = 2;
        private static final int WEST = 3;
        int direction = EAST;
        int x;
        int y;

        public void apply(Instruction instruction) {
            switch (instruction.direction) {
                case 'N' -> y -= instruction.count;
                case 'S' -> y += instruction.count;
                case 'E' -> x += instruction.count;
                case 'W' -> x -= instruction.count;
                case 'R' -> direction = (direction + instruction.count / 90) % 4;
                case 'L' -> direction = (direction + 4 - instruction.count / 90) % 4;
                case 'F' -> {
                    switch (direction) {
                        case NORTH -> y -= instruction.count;
                        case SOUTH -> y += instruction.count;
                        case EAST -> x += instruction.count;
                        case WEST -> x -= instruction.count;
                    }
                }
            }
        }

        public int manhattanDistance() {
            return Math.abs(x) + Math.abs(y);
        }
    }

    record Instruction(char direction, int count) {
        static Instruction parse(String line) {
            return new Instruction(line.charAt(0), Integer.parseInt(line.substring(1)));
        }
    }
}
