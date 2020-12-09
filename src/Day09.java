import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Day09 {
    private static final int ROLLING_SIZE = 25;

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input09.txt"));
        var numbers = input.stream()
            .map(Long::parseLong)
            .collect(Collectors.toList());

        var target = part01(numbers);
        part02(numbers, target);
    }

    private static long part01(List<Long> numbers) {
        for (int i = ROLLING_SIZE; i < numbers.size(); i++) {
            if (!xmasSum(numbers, i)) {
                var number = numbers.get(i);
                System.out.println(number);
                return number;
            }
        }
        return -1;
    }

    private static void part02(List<Long> numbers, long target) {
        for (int i = 0; i < numbers.size() - 1; i++) {
            var sum = numbers.get(i);
            var min = sum;
            var max = sum;

            for (int j = i + 1; j < numbers.size(); j++) {
                var current = numbers.get(j);
                sum += current;
                if (current < min) {
                    min = current;
                }
                if (current > max) {
                    max = current;
                }

                if (sum == target) {
                    System.out.println(min + max);
                    return;
                } else if (sum > target) {
                    break;
                }
            }
        }
    }

    private static boolean xmasSum(List<Long> numbers, int index) {
        var number = numbers.get(index);
        for (int i = index - ROLLING_SIZE; i < index - 1; i++) {
            var first = numbers.get(i);
            for (int j = i + 1; j < index; j++) {
                if (first + numbers.get(j) == number) return true;
            }
        }
        return false;
    }
}
