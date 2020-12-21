import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day21 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input21.txt"));

        var foods = input.stream()
            .map(Food::parse)
            .collect(Collectors.toList());

        var allergenIn = new HashMap<String, List<String>>();
        foods.forEach(food -> food.allergens.forEach(allergen -> {
            if (!allergenIn.containsKey(allergen)) {
                allergenIn.put(allergen, new ArrayList<>(food.ingredients));
            } else {
                allergenIn.get(allergen).removeIf(item -> !food.ingredients.contains(item));
            }
        }));
        while (allergenIn.values().parallelStream().anyMatch(list -> list.size() > 1)) {
            var unique = allergenIn.values()
                .parallelStream()
                .filter(list -> list.size() == 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            allergenIn.values()
                .parallelStream()
                .filter(list -> list.size() > 1)
                .forEach(list -> list.removeAll(unique));
        }
        var allergenFoods = allergenIn.entrySet()
            .parallelStream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));

        part01(foods, allergenFoods);
        part02(allergenFoods);
    }

    private static void part01(List<Food> foods, Map<String, String> allergenFoods) {
        var allIngredients = foods.parallelStream()
            .map(Food::ingredients)
            .flatMap(Collection::stream)
            .filter(food -> !allergenFoods.containsValue(food))
            .collect(Collectors.toList());

        System.out.println(allIngredients.size());
    }

    private static void part02(Map<String, String> allergenFoods) {
        var result = allergenFoods.entrySet()
            .parallelStream()
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .collect(Collectors.joining(","));
        System.out.println(result);
    }

    record Food(List<String> ingredients, List<String> allergens) {
        static Food parse(String line) {
            var parts = line.split(" \\(contains ");
            var ingredients = Arrays.asList(parts[0].split(" "));
            List<String> allergens;
            if (parts.length == 2) {
                allergens = Arrays.asList(parts[1].replaceAll("\\)", "")
                    .split(", "));
            } else {
                allergens = new ArrayList<>();
            }
            return new Food(ingredients, allergens);
        }
    }
}
