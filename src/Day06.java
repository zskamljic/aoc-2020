import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Day06 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input06.txt"));
        var groups = input.split("\n\n");

        part01(groups);
        part02(groups);
    }

    private static void part01(String[] groups) {
        var sum = 0;
        for (var group : groups) {
            var characters = group.replaceAll("[^\\w]", "").split("");
            sum += Arrays.stream(characters)
                .distinct()
                .count();
        }
        System.out.println(sum);
    }

    private static void part02(String[] groups) {
        var sum = 0;

        for (var group : groups) {
            var people = group.split("\n");
            var lists = Arrays.stream(people)
                .map(person -> Arrays.asList(person.split("")))
                .collect(Collectors.toList());

            var intersection = new ArrayList<>(lists.remove(0));
            for (var person : lists) {
                intersection.retainAll(person);
            }
            sum += intersection.size();
        }
        System.out.println(sum);
    }
}
