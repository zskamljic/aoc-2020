import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;

public class Day14 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input14.txt"));

        String mask = "X".repeat(36);
        var instructions = new ArrayList<Instruction>();
        for (var line : input) {
            if (line.startsWith("mask = ")) {
                mask = extractMask(line);
                continue;
            }

            var split = line.split("\\s");
            var address = Integer.parseInt(split[0].replaceAll("[^\\d+]", ""));
            var binary = Integer.toBinaryString(Integer.parseInt(split[2]));
            var value = "0".repeat(36 - binary.length()) + binary;

            instructions.add(new Instruction(address, value, mask));
        }

        part01(instructions);
        part02(instructions);
    }

    private static void part01(List<Instruction> instructions) {
        commonCompute(instructions, Instruction::applyV1);
    }

    private static void part02(List<Instruction> instructions) {
        commonCompute(instructions, Instruction::applyV2);
    }

    private static void commonCompute(List<Instruction> instructions, BiConsumer<Instruction,
        Map<Long, Long>> evaluator) {
        var memory = new HashMap<Long, Long>();
        instructions.forEach(instruction -> evaluator.accept(instruction, memory));
        var sum = memory.values()
            .stream()
            .mapToLong(value -> value)
            .sum();
        System.out.println(sum);
    }

    private static String extractMask(String mask) {
        return mask.split("\\s")[2];
    }

    record Instruction(int address, String value, String mask) {
        private long applyMask() {
            var characters = value.toCharArray();
            for (int i = 0; i < mask.length(); i++) {
                if (mask.charAt(i) == 'X') continue;

                characters[i] = mask.charAt(i);
            }
            return Long.parseLong(new String(characters), 2);
        }

        public void applyV1(Map<Long, Long> memory) {
            memory.put((long) address, applyMask());
        }

        public void applyV2(Map<Long, Long> memory) {
            var value = Long.parseLong(value(), 2);
            var addresses = generateAddressList();
            addresses.forEach(address -> memory.put(address, value));
        }

        private List<Long> generateAddressList() {
            var addressBin = Integer.toBinaryString(address);
            addressBin = "0".repeat(36 - addressBin.length()) + addressBin;
            var address = addressBin.toCharArray();
            var mask = mask().toCharArray();
            for (int i = 0; i < address.length; i++) {
                if (mask[i] != '0') {
                    address[i] = mask[i];
                }
            }

            var addresses = new ArrayList<Long>();
            var queue = new ArrayDeque<String>();
            queue.add(new String(address));

            while (!queue.isEmpty()) {
                var next = queue.poll();

                boolean replaced = false;
                var array = next.toCharArray();
                for (int i = 0; i < array.length; i++) {
                    if (array[i] != 'X') continue;

                    replaced = true;
                    array[i] = '0';
                    queue.add(new String(array));
                    array[i] = '1';
                    queue.add(new String(array));
                    break;
                }
                if (!replaced) {
                    addresses.add(Long.parseLong(next, 2));
                }
            }

            return addresses;
        }
    }

}
