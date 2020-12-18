import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Day18 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input18.txt"));

        part01(input);
        part02(input);
    }

    private static void part01(List<String> input) {
        var result = input.parallelStream()
            .map(Day18::tokenize)
            .mapToLong(Day18::evaluateLtr)
            .sum();
        System.out.println(result);
    }

    private static void part02(List<String> input) {
        var result = input.parallelStream()
            .map(Day18::tokenize)
            .mapToLong(Day18::evaluatePrecedence)
            .sum();
        System.out.println(result);
    }

    private static long evaluateLtr(List<String> tokens) {
        var queue = new ArrayDeque<>(tokens);

        var results = new Stack<Long>();
        var operators = new Stack<Operator>();
        var result = 0L;
        var operator = Operator.PLUS;
        while (!queue.isEmpty()) {
            var token = queue.poll();
            if (token.matches("\\d+")) {
                result = apply(result, operator, Long.parseLong(token));
            } else if (token.equals("+")) {
                operator = Operator.PLUS;
            } else if (token.equals("*")) {
                operator = Operator.MULTIPLY;
            } else if (token.equals("(")) {
                results.push(result);
                operators.push(operator);
                result = 0;
                operator = Operator.PLUS;
            } else if (token.equals(")")) {
                result = apply(results.pop(), operators.pop(), result);
            }
        }
        return result;
    }

    private static long evaluatePrecedence(List<String> tokens) {
        var queue = new ArrayDeque<>(tokens);

        var values = new Stack<Long>();
        var operators = new Stack<String>();
        while (!queue.isEmpty()) {
            var token = queue.poll();

            if (token.matches("\\d+")) {
                values.push(Long.parseLong(token));
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.peek().equals("(")) {
                    evaluateOperators(operators, values);
                }
                operators.pop();
            } else if (token.equals("+") || token.equals("*")) {
                while (sameOrGreaterPrecedence(operators, token)) {
                    evaluateOperators(operators, values);
                }
                operators.push(token);
            }
        }
        while (!operators.isEmpty()) {
            evaluateOperators(operators, values);
        }
        return values.pop();
    }

    private static void evaluateOperators(Stack<String> operators, Stack<Long> values) {
        var operator = operators.pop();
        var right = values.pop();
        var left = values.pop();
        var result = switch (operator) {
            case "+" -> left + right;
            case "*" -> left * right;
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
        values.push(result);
    }

    private static boolean sameOrGreaterPrecedence(Stack<String> stack, String token) {
        if (stack.isEmpty()) return false;

        var top = stack.peek();
        return switch (top) {
            case "+" -> true;
            case "*" -> !token.equals("+");
            default -> !token.equals("+") && !token.equals("*");
        };
    }

    private static long apply(long result, Operator operator, long value) {
        return switch (operator) {
            case PLUS -> result + value;
            case MULTIPLY -> result * value;
        };
    }

    private static List<String> tokenize(String input) {
        var tokens = new ArrayList<String>();

        var parts = input.split("\\s+");
        for (var part : parts) {
            if (part.startsWith("(") || part.endsWith(")")) {
                tokens.addAll(split(part));
            } else {
                tokens.add(part);
            }
        }
        return tokens;
    }

    private static List<String> split(String token) {
        var result = new ArrayList<String>();

        while (token.startsWith("(")) {
            result.add("(");
            token = token.substring(1);
        }
        if (token.endsWith(")")) {
            result.add(token.substring(0, token.indexOf(")")));
            while (token.endsWith(")")) {
                result.add(")");
                token = token.substring(0, token.length() - 1);
            }
        } else {
            result.add(token);
        }

        return result;
    }

    enum Operator {
        PLUS,
        MULTIPLY
    }
}
