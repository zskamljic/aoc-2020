import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Day13 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input13.txt"))
            .split("\n");

        var minDeparture = Integer.parseInt(input[0]);
        part01(minDeparture, input[1]);
        part02(input[1]);
    }

    private static void part01(int minDeparture, String buses) {
        var busIds = Arrays.stream(buses.split(","))
            .filter(line -> line.matches("\\d+"))
            .map(Integer::parseInt)
            .collect(Collectors.toList());

        var departure = busIds.parallelStream()
            .map(value -> minAbove(value, minDeparture))
            .min(Comparator.comparing(Departure::waitTime))
            .map(Departure::part01)
            .orElseThrow();

        System.out.println(departure);
    }

    private static void part02(String input) {
        var buses = input.split(",");

        var conditions = new ArrayList<Interval>();
        for (int i = 0; i < buses.length; i++) {
            if ("x".equals(buses[i])) continue;

            conditions.add(new Interval(Integer.parseInt(buses[i]), i));
        }

        var element = conditions.remove(0);
        var currentBuses = new ArrayList<Long>();
        currentBuses.add(element.factor);
        long increment = element.factor;
        long minTime = element.factor;
        while (!conditions.isEmpty()) {
            var nextElement = conditions.remove(0);
            while (!nextElement.matches(minTime)) {
                minTime += increment;
            }
            currentBuses.add(nextElement.factor);
            increment = leastCommonMultiple(currentBuses);
        }
        System.out.println(minTime);
    }

    private static Departure minAbove(int rate, int minDeparture) {
        return new Departure(rate, (minDeparture / rate + 1) * rate - minDeparture);
    }

    private static long leastCommonMultiple(ArrayList<Long> values) {
        var array = values.stream()
            .mapToLong(value -> value)
            .toArray();
        var result = array[0];
        for (int i = 1; i < array.length; i++) {
            result = leastCommonMultiple(result, array[i]);
        }
        return result;
    }

    private static long leastCommonMultiple(long a, long b) {
        return a * (b / greatestCommonDivisor(a, b));
    }

    private static long greatestCommonDivisor(long a, long b) {
        while (b > 0) {
            var temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    record Departure(int busId, int waitTime) {
        int part01() {
            return busId * waitTime;
        }
    }

    record Interval(long factor, long offset) {
        boolean matches(long time) {
            return (time + offset) % factor == 0;
        }
    }
}
