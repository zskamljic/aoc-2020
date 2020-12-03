import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day03 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input03.txt"));

        // Part 01
        System.out.println(findTrees(lines, new Slope(3, 1)));
        // Part 02
        var slopes = List.of(
                new Slope(1, 1),
                new Slope(3, 1),
                new Slope(5, 1),
                new Slope(7, 1),
                new Slope(1, 2)
        );
        var all = slopes.parallelStream()
                .map(slope -> findTrees(lines, slope))
                .reduce(1, (accumulator, value) -> accumulator * value);
        System.out.println(all);
    }

    private static int findTrees(List<String> lines, Slope slope) {
        int trees = 0;
        int x = 0;
        for (int i = slope.y; i < lines.size(); i += slope.y) {
            x += slope.x;
            x %= lines.get(i).length();
            if (lines.get(i).charAt(x) == '#') {
                trees++;
            }
        }
        return trees;
    }

    record Slope(int x, int y) {
    }
}
