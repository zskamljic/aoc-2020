import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Day01 {
    public static void main(String[] args) throws IOException {
        var items = Files.readAllLines(Paths.get("input01.txt"))
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        part01(items);
        part02(items);
    }

    private static void part01(List<Integer> items) {
        for (int i = 0; i < items.size() - 1; i++) {
            for (int j = i + 1; j < items.size(); j++) {
                var first = items.get(i);
                var second = items.get(j);
                var sum = first + second;

                if (sum == 2020) {
                    System.out.println(first * second);
                    return;
                }
            }
        }
    }

    private static void part02(List<Integer> items) {
        for (int i = 0; i < items.size() - 2; i++) {
            for (int j = i + 1; j < items.size() - 1; j++) {
                for (int k = j + 1; k < items.size(); k++) {
                    var first = items.get(i);
                    var second = items.get(j);
                    var third = items.get(k);
                    var sum = first + second + third;

                    if (sum == 2020) {
                        System.out.println(first * second * third);
                        return;
                    }
                }
            }
        }
    }
}
