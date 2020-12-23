import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day23 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input23.txt"));

        var circle = Arrays.stream(input.split(""))
            .map(Integer::parseInt)
            .collect(Collectors.toCollection(ArrayList::new));
        var min = circle.stream()
            .mapToInt(i -> i)
            .min()
            .orElseThrow();
        var max = circle.stream()
            .mapToInt(i -> i)
            .max()
            .orElseThrow();

        part01(min, max, circle);
        var before = System.currentTimeMillis();
        part02(min, max, circle);
        var after = System.currentTimeMillis();
        System.out.println(after-before);
    }

    private static void part01(int min, int max, List<Integer> circle) {
        var lookup = solveCommon(min, max, 100, circle);

        var builder = new StringBuilder();
        var one = lookup[1];
        var current = one.next;
        while (current != one) {
            builder.append(current.value);
            current = current.next;
        }
        System.out.println(builder.toString());
    }

    private static void part02(int min, int max, List<Integer> circle) {
        for (int i = max + 1; i <= 1_000_000; i++) {
            circle.add(i);
        }

        var lookup = solveCommon(min, 1_000_000, 10_000_000, circle);
        var one = lookup[1];
        var next = one.next;
        var nextNext = next.next;

        System.out.print(next.value + " * " + nextNext.value + " = ");
        System.out.println(next.value * (long) nextNext.value);
    }

    private static Node[] solveCommon(int min, int max, int iterations, List<Integer> circle) {
        var nodes = circle.stream()
            .mapToInt(i -> i)
            .mapToObj(i -> {
                var node = new Node();
                node.value = i;
                return node;
            })
            .collect(Collectors.toList());

        var head = nodes.remove(0);
        var lookup = new Node[max + 1];
        lookup[head.value] = head;

        var previous = head;
        for (var node : nodes) {
            previous.next = node;
            previous = node;
            lookup[node.value] = node;
        }
        previous.next = head;

        var current = head;
        for (int i = 0; i < iterations; i++) {
            var threeClip = current.next;
            current.next = threeClip.next.next.next;

            var destinationValue = current.value - 1;
            while (threeClip.value == destinationValue ||
                threeClip.next.value == destinationValue ||
                threeClip.next.next.value == destinationValue ||
                destinationValue < min) {

                destinationValue--;
                if (destinationValue < min) {
                    destinationValue = max;
                }
            }
            var target = lookup[destinationValue];
            threeClip.next.next.next = target.next;
            target.next = threeClip;
            current = current.next;
        }
        return lookup;
    }

    static class Node {
        int value;
        Node next;
    }
}
