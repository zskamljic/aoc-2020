import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day08 {
    public static void main(String[] args) throws IOException {
        var instructions = Files.readAllLines(Paths.get("input08.txt"))
            .stream()
            .map(Instruction::parse)
            .collect(Collectors.toList());

        part01(instructions);
        System.out.println();
        part02(instructions);
    }

    private static void part01(List<Instruction> instructions) {
        var program = new Program(instructions);
        program.execute(true);
    }

    private static void part02(List<Instruction> instructions) {
        IntStream.range(0, instructions.size())
            .parallel()
            .mapToObj(i -> swap(instructions, i))
            .filter(Objects::nonNull)
            .map(Program::new)
            .map(Program::execute)
            .filter(Objects::nonNull)
            .findFirst()
            .ifPresent(System.out::println);
    }

    private static List<Instruction> swap(List<Instruction> instructions, int index) {
        var instruction = instructions.get(index);
        if (instruction.op == Operation.ACC) return null;

        var modified = new ArrayList<>(instructions);
        var replacement = new Instruction(instruction.op == Operation.NOP ? Operation.JMP : Operation.NOP, instruction.argument);
        modified.set(index, replacement);
        return modified;
    }

    static class Program {
        int accumulator;
        int pc;
        List<Instruction> instructions;

        Program(List<Instruction> instructions) {
            this.instructions = instructions;
        }

        Integer execute() {
            return execute(false);
        }

        Integer execute(boolean printTerminating) {
            var executed = new HashSet<Integer>();

            while (pc < instructions.size()) {
                var instruction = instructions.get(pc);

                if (executed.contains(pc)) {
                    if (printTerminating) {
                        System.out.println(accumulator);
                    }
                    return null;
                }
                executed.add(pc);

                switch (instruction.op) {
                    case ACC -> accumulator += instruction.argument;
                    case JMP -> {
                        pc += instruction.argument;
                        continue;
                    }
                }
                pc++;
            }
            return accumulator;
        }
    }

    record Instruction(Operation op, int argument) {
        public static Instruction parse(String line) {
            var parts = line.split("\s");
            return new Instruction(Operation.fromString(parts[0]), Integer.parseInt(parts[1]));
        }
    }

    enum Operation {
        ACC,
        JMP,
        NOP;

        static Operation fromString(String command) {
            return switch (command) {
                case "nop" -> NOP;
                case "jmp" -> JMP;
                case "acc" -> ACC;
                default -> throw new UnsupportedOperationException("Unknown operation: " + command);
            };
        }
    }
}
