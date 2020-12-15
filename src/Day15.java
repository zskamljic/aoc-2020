import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day15 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input15.txt"));

        var numbers = Arrays.stream(input.split(","))
            .map(Integer::parseInt)
            .collect(Collectors.toCollection(ArrayList::new));
        part01(numbers);
        part02(numbers);
    }

    private static void part01(List<Integer> numbers) {
        var turn = numbers.size();
        var lastNumber = numbers.get(numbers.size() - 1);
        while (turn < 2020) {
            var next = 0;
            for (int i = numbers.size() - 1; i > 0; i--) {
                if (Objects.equals(numbers.get(i - 1), lastNumber)) {
                    next = turn - i;
                    break;
                }
            }
            numbers.add(next);
            lastNumber = next;
            turn++;
        }
        System.out.println(lastNumber);
    }

    private static void part02(List<Integer> numbers) {
        var lastSeen = new HashMap<Integer, Integer>();
        for (int i = 0; i < numbers.size() - 1; i++) {
            lastSeen.put(numbers.get(i), i + 1);
        }

        var turn = numbers.size();
        var lastNumber = numbers.get(numbers.size() - 1);
        while (turn < 30_000_000) {
            var next = turn - lastSeen.getOrDefault(lastNumber, turn);
            lastSeen.put(lastNumber, turn);
            turn++;
            lastNumber = next;
        }
        System.out.println(lastNumber);
    }
}
