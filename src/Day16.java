import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day16 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input16.txt"));
        var parts = input.split("\n\n");

        var constraints = Arrays.stream(parts[0].split("\n"))
            .map(Constraint::parse)
            .collect(Collectors.toList());

        var tickets = Arrays.stream(parts[2].split("\n"))
            .skip(1) // Skip the region header
            .map(Day16::toTicketArray)
            .toArray(int[][]::new);

        part01(constraints, tickets);

        var ticketString = parts[1].split("\n")[1]
            .split(",");
        var ticket = Arrays.stream(ticketString)
            .mapToInt(Integer::parseInt)
            .toArray();
        part02(constraints, tickets, ticket);
    }

    private static void part01(List<Constraint> constraints, int[][] tickets) {
        var sum = 0;
        for (var ticket : tickets) {
            for (var field : ticket) {
                if (constraints.stream().noneMatch(c -> c.isValid(field))) {
                    sum += field;
                }
            }
        }
        System.out.println(sum);
    }

    private static void part02(List<Constraint> constraints, int[][] allTickets, int[] ticket) {
        var validTickets = Arrays.stream(allTickets)
            .filter(t -> isTicketValid(constraints, t))
            .toArray(int[][]::new);

        var columnTitles = new HashMap<Integer, List<String>>();

        for (int i = 0; i < ticket.length; i++) {
            int currentColumn = i;
            for (var constraint : constraints) {
                if (Arrays.stream(validTickets)
                    .mapToInt(row -> row[currentColumn])
                    .allMatch(constraint::isValid)) {
                    columnTitles.compute(i, (k, v) -> {
                        var list = v == null ? new ArrayList<String>() : v;
                        list.add(constraint.key);
                        return list;
                    });
                }
            }
        }

        do {
            var singles = columnTitles.values()
                .parallelStream()
                .filter(list -> list.size() == 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            columnTitles.values()
                .parallelStream()
                .filter(list -> list.size() > 1)
                .forEach(list -> list.removeAll(singles));
        } while (hasMultipleCandidates(columnTitles));

        var titles = new String[columnTitles.size()];
        columnTitles.forEach((key, value) -> titles[key] = value.get(0));

        var product = 1L;
        for (int i = 0; i < titles.length; i++) {
            if (titles[i] == null || !titles[i].startsWith("departure")) continue;

            product *= ticket[i];
        }

        System.out.println(product);
    }

    private static boolean hasMultipleCandidates(Map<Integer, List<String>> titles) {
        return titles.values().stream().anyMatch(entry -> entry.size() > 1);
    }

    private static boolean isTicketValid(List<Constraint> constraints, int[] ticket) {
        for (var field : ticket) {
            if (constraints.stream().noneMatch(c -> c.isValid(field))) {
                return false;
            }
        }
        return true;
    }

    private static int[] toTicketArray(String line) {
        return Arrays.stream(line.split(","))
            .mapToInt(Integer::parseInt)
            .toArray();
    }

    record Constraint(String key, Range[] ranges) {
        static Constraint parse(String line) {
            var parts = line.split(": ");
            var key = parts[0];
            var values = Range.parse(parts[1]);
            return new Constraint(key, values);
        }

        boolean isValid(int number) {
            return ranges[0].isValid(number) || ranges[1].isValid(number);
        }
    }

    record Range(int min, int max) {
        static Range[] parse(String condition) {
            var parts = condition.split(" or ");
            var result = new Range[parts.length];

            for (int i = 0; i < parts.length; i++) {
                var values = parts[i].split("-");
                result[i] = new Range(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
            }
            return result;
        }

        boolean isValid(int value) {
            return value >= min && value <= max;
        }
    }
}
