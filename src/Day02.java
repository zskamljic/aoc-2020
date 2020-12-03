import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day02 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input02.txt"))
                .stream()
                .map(PasswordSpec::parse)
                .collect(Collectors.toList());

        System.out.println(commonPart(lines, Day02::part01));
        System.out.println(commonPart(lines, Day02::part02));
    }

    public static boolean part01(PasswordSpec spec) {
        var reduced = spec.password.replaceAll("[^" + spec.character + "]", "");
        return reduced.length() >= spec.first && reduced.length() <= spec.second;
    }

    public static boolean part02(PasswordSpec spec) {
        return spec.password.charAt(spec.first - 1) == spec.character ^ spec.password.charAt(spec.second - 1) == spec.character;
    }

    private static long commonPart(List<PasswordSpec> specs, Predicate<PasswordSpec> isValid) {
        return specs.parallelStream()
                .filter(isValid)
                .count();
    }

    record PasswordSpec(int first, int second, char character, String password) {
        static PasswordSpec parse(String line) {
            var parts = line.split(": ");
            var ruleSpec = parts[0].split(" ");
            var repeats = Arrays.stream(ruleSpec[0].split("-"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return new PasswordSpec(repeats.get(0), repeats.get(1), ruleSpec[1].charAt(0), parts[1]);
        }
    }
}
