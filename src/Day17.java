import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Day17 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input17.txt"));

        part01(input);
        part02(input);
    }

    private static void part01(List<String> input) {
        solve(input, (x, y) -> new Point3d(x, y, 0));
    }

    private static void part02(List<String> input) {
        solve(input, (x, y) -> new Point4d(x, y, 0, 0));
    }

    private static <T extends NeighbourProvider<T>> void solve(List<String> input, BiFunction<Integer, Integer, T> identityCreator) {
        List<T> pocketDimension = new ArrayList<>();
        for (int y = 0; y < input.size(); y++) {
            var line = input.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    pocketDimension.add(identityCreator.apply(x, y));
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            pocketDimension = cycle(pocketDimension);
        }
        System.out.println(pocketDimension.size());
    }

    private static <T extends NeighbourProvider<T>> List<T> cycle(List<T> living) {
        var newLiving = new CopyOnWriteArrayList<T>();

        var fields = living.parallelStream()
            .map(NeighbourProvider::neighbours)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());

        fields.parallelStream()
            .forEach(point -> {
                var neighbours = point.neighbours()
                    .stream()
                    .filter(p -> !p.equals(point))
                    .collect(Collectors.toList());
                var livingNeighbours = living.stream()
                    .filter(neighbours::contains)
                    .count();
                if (living.contains(point)) {
                    if (livingNeighbours == 2 || livingNeighbours == 3) {
                        newLiving.add(point);
                    }
                } else {
                    if (livingNeighbours == 3) {
                        newLiving.add(point);
                    }
                }
            });

        return newLiving;
    }

    interface NeighbourProvider<T> {
        Collection<T> neighbours();
    }

    record Point3d(int x, int y, int z) implements NeighbourProvider<Point3d> {
        @Override
        public Collection<Point3d> neighbours() {
            var neighbours = new ArrayList<Point3d>();
            for (int z = -1; z < 2; z++) {
                for (int y = -1; y < 2; y++) {
                    for (int x = -1; x < 2; x++) {
                        neighbours.add(new Point3d(this.x + x, this.y + y, this.z + z));
                    }
                }
            }
            return neighbours;
        }
    }

    record Point4d(int x, int y, int z, int w) implements NeighbourProvider<Point4d> {
        @Override
        public Collection<Point4d> neighbours() {
            var neighbours = new ArrayList<Point4d>();
            for (int w = -1; w < 2; w++) {
                for (int z = -1; z < 2; z++) {
                    for (int y = -1; y < 2; y++) {
                        for (int x = -1; x < 2; x++) {
                            neighbours.add(new Point4d(this.x + x, this.y + y, this.z + z, this.w + w));
                        }
                    }
                }
            }
            return neighbours;
        }
    }
}
