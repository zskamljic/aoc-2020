import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day07 {
    private static final String QUERY = "shiny gold";

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input07.txt"));
        var rules = lines.parallelStream()
            .map(Rule::parse)
            .collect(Collectors.toMap(Rule::container, Rule::contained));

        part01(rules);
        part02(rules);
    }

    private static void part01(Map<String, List<Contained>> rules) {
        var validContainers = rules.entrySet()
            .stream()
            .filter(Day07::filterGolden)
            .map(Map.Entry::getKey)
            .collect(Collectors.toCollection(HashSet::new));

        var candidates = new ArrayDeque<>(validContainers);
        while (!candidates.isEmpty()) {
            var container = candidates.poll();
            var containing = findAllContaining(rules, container);
            candidates.addAll(containing);
            validContainers.addAll(containing);
        }

        System.out.println(validContainers.size());
    }

    private static void part02(Map<String, List<Contained>> rules) {
        int count = 0;
        var queue = new ArrayDeque<>(rules.get(QUERY));

        while (!queue.isEmpty()) {
            var item = queue.poll();
            count += item.quantity();
            var children = rules.get(item.color)
                .parallelStream()
                .map(contained -> contained.multiply(item.quantity()))
                .collect(Collectors.toList());
            queue.addAll(children);
        }
        System.out.println(count);
    }

    private static boolean filterGolden(Map.Entry<String, List<Contained>> entry) {
        return entry.getValue()
            .parallelStream()
            .anyMatch(Contained::isGoldenBag);
    }

    private static List<String> findAllContaining(Map<String, List<Contained>> rules, String container) {
        return rules.entrySet()
            .parallelStream()
            .filter(entry -> entry.getValue().stream().anyMatch(contained -> contained.color().equals(container)))
            .map(Map.Entry::getKey)
            .distinct()
            .collect(Collectors.toList());
    }

    record Rule(String container, List<Contained> contained) {
        public static Rule parse(String input) {
            var scanner = new Scanner(input);
            // color
            var color = scanner.next() + " " + scanner.next();
            // bags
            scanner.next();
            // contain
            scanner.next();
            var contained = new ArrayList<Contained>();
            while (scanner.hasNext()) {
                contained.add(Contained.parse(scanner));
            }
            contained.removeIf(Objects::isNull);
            return new Rule(color, contained);
        }
    }

    record Contained(String color, int quantity) {
        public static Contained parse(Scanner scanner) {
            var quantityString = scanner.next();
            if ("no".equals(quantityString)) {
                // no other bags
                scanner.nextLine();
                return null;
            }

            var quantity = Integer.parseInt(quantityString);
            var color = scanner.next() + " " + scanner.next();
            // bag, bags, bags.
            scanner.next();
            return new Contained(color, quantity);
        }

        public boolean isGoldenBag() {
            return color.equals(QUERY);
        }

        public Contained multiply(int quantity) {
            return new Contained(color, this.quantity * quantity);
        }
    }
}
