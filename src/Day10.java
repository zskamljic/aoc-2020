import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day10 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input10.txt"));

        var adapters = input.stream()
            .mapToInt(Integer::parseInt)
            .sorted()
            .boxed()
            .collect(Collectors.toList());

        part01(adapters);
        part02(adapters);
    }

    private static void part01(List<Integer> adapters) {
        var currentJoltage = 0;
        var singles = 0;
        var triples = 0;
        for (var adapter : adapters) {
            if (adapter - currentJoltage < 4) {
                if (adapter - currentJoltage == 1) {
                    singles++;
                } else if (adapter - currentJoltage == 3) {
                    triples++;
                }
                currentJoltage = adapter;
            }
        }
        // Device joltage
        triples++;
        System.out.println(singles * triples);
    }

    private static void part02(List<Integer> adapters) {
        var fullList = new ArrayList<>(adapters);
        // multiple starting points, make it so that there is unique start & end
        var max = adapters.stream()
            .max(Integer::compareTo)
            .orElseThrow();
        fullList.add(0, 0);
        fullList.add(max + 3);

        // All nodes and count of ways to reach them
        var paths = new long[fullList.size()];
        paths[0] = 1;

        // Try to use every adapter with every other
        for (int i = 0; i < paths.length - 1; i++) {
            for (int j = i + 1; j < paths.length; j++) {
                // Skip early, since list is sorted if we reached
                // first one over max difference there's not going to be any more
                if (fullList.get(j) - fullList.get(i) > 3) break;

                // Add all the paths from previous point to this one
                paths[j] += paths[i];
            }
        }

        System.out.println(paths[paths.length - 1]);
    }
}
