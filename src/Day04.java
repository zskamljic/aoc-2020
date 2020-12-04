import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Day04 {
    // cid is optional
    private static final List<String> requiredFields = List.of(
        "byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid"
    );

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input04.txt"));
        var passports = input.split("\n\n");

        part01(passports);
        part02(passports);
    }

    private static void part01(String[] passports) {
        var valid = 0;
        passports:
        for (var passport : passports) {
            for (var field : requiredFields) {
                if (!passport.contains(field)) {
                    continue passports;
                }
            }
            valid++;
        }
        System.out.println(valid);
    }

    private static void part02(String[] passports) {
        var count = Arrays.stream(passports)
            .map(Passport::parse)
            .filter(Passport::isValid)
            .count();
        System.out.println(count);
    }

    record Passport(
        String birthYear,
        String issueYear,
        String expirationYear,
        Height height,
        String hairColor,
        String eyeColor,
        String pid
    ) {
        static Passport parse(String input) {
            var fields = input.split("(\\s+)");
            var dataMap = new HashMap<String, String>();
            for (var field : fields) {
                var parts = field.split(":");
                dataMap.put(parts[0], parts[1]);
            }
            return new Passport(
                dataMap.get("byr"),
                dataMap.get("iyr"),
                dataMap.get("eyr"),
                Height.parse(dataMap.get("hgt")),
                dataMap.get("hcl"),
                dataMap.get("ecl"),
                dataMap.get("pid")
            );
        }

        boolean isValid() {
            return validNumber(birthYear, 1920, 2002) &&
                validNumber(issueYear, 2010, 2020) &&
                validNumber(expirationYear, 2020, 2030) &&
                height != null && height.isValid() &&
                validColor(hairColor) &&
                validEyeColor(eyeColor) &&
                pid != null && pid.matches("[0-9]{9}");
        }

        private boolean validNumber(String string, int min, int max) {
            try {
                var number = Integer.parseInt(string);
                return number >= min && number <= max;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean validColor(String color) {
            return color != null && color.matches("#[0-9a-f]{6}");
        }

        private boolean validEyeColor(String eyeColor) {
            return eyeColor != null && List.of("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(eyeColor);
        }
    }

    record Height(int height, String unit) {
        static Height parse(String input) {
            try {
                var height = Integer.parseInt(input.replaceAll("[^\\d]", ""));
                var unit = input.replaceAll("[^incm]", "");
                return new Height(height, unit);
            } catch (Exception e) {
                return null;
            }
        }

        boolean isValid() {
            if ("cm".equals(unit)) {
                return height >= 150 && height <= 193;
            }
            if ("in".equals(unit)) {
                return height >= 59 && height <= 76;
            }
            return false;
        }
    }
}
