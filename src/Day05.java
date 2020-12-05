import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Day05 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input05.txt"));

        part01(lines);
        part02(lines);
    }

    private static void part01(java.util.List<String> lines) {
        var maxSeat = lines.parallelStream()
            .mapToInt(Day05::idForSeat)
            .max().orElseThrow();
        System.out.println(maxSeat);
    }

    private static void part02(List<String> lines) {
        var seats = lines.parallelStream()
            .mapToInt(Day05::idForSeat)
            .sorted()
            .toArray();
        int previous = seats[0];
        for (int i = 1; i < seats.length; i++) {
            if (seats[i] - previous != 1) {
                System.out.println(seats[i] - 1);
                return;
            }
            previous = seats[i];
        }
    }

    private static int idForSeat(String seat) {
        int row = searchRow(seat);
        int column = searchColumn(seat);
        return getId(row, column);
    }

    private static int getId(int row, int column) {
        return row * 8 + column;
    }

    private static int searchRow(String input) {
        return search(input.substring(0, 7), 127);
    }

    private static int searchColumn(String input) {
        return search(input.substring(7), 7);
    }

    private static int search(String input, int max) {
        int min = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == 'F' || input.charAt(i) == 'L') {
                max -= Math.ceil((max - min) / 2f);
            }
            if (input.charAt(i) == 'B' || input.charAt(i) == 'R') {
                min += Math.ceil((max - min) / 2f);
            }
        }
        return min;
    }
}
