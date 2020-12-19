import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day19 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input19.txt"))
            .split("\n\n");

        var ruleSpecs = new HashMap<Integer, String>();
        for (var rule : input[0].split("\n")) {
            var index = Integer.parseInt(rule.split(": ")[0]);
            ruleSpecs.put(index, rule);
        }

        var messages = input[1].split("\n");
        part01(ruleSpecs, messages);
        part02(ruleSpecs, messages);
    }

    private static void part01(Map<Integer, String> ruleSpecs, String[] messages) {
        var rule = parseRules(ruleSpecs);

        var matching = Arrays.stream(messages)
            .map(rule::match)
            .filter(Result::isValid)
            .filter(r -> r instanceof Valid v && v.remainder.isEmpty())
            .count();

        System.out.println(matching);
    }

    private static void part02(Map<Integer, String> ruleSpecs, String[] messages) {
        ruleSpecs = new HashMap<>(ruleSpecs);
        ruleSpecs.put(8, "8: 42 | 42 8");
        ruleSpecs.put(11, "11: 42 31 | 42 11 31");

        var rule = parseRules(ruleSpecs);
        var regex = rule.toRegex();

        var count = Arrays.stream(messages)
            .filter(message -> message.matches(regex))
            .count();
        System.out.println(count);
    }

    private static Rule parseRules(Map<Integer, String> ruleSpecs) {
        var rules = new HashMap<Integer, Rule>();
        return parseRule(ruleSpecs, rules, 0);
    }

    private static Rule parseRule(Map<Integer, String> ruleSpecs, HashMap<Integer, Rule> rules, int index) {
        if (rules.containsKey(index)) return rules.get(index);

        var parts = ruleSpecs.get(index).split(": ");
        var ruleName = Integer.parseInt(parts[0]);
        var definition = parts[1];

        if (definition.matches("\"[a-z]+\"")) {
            var rule = new Literal(ruleName, definition.replaceAll("\"", ""));
            rules.put(index, rule);
            return rule;
        } else if (definition.contains("|")) {
            var definitions = Arrays.stream(definition.split("\\|"))
                .parallel()
                .map(String::trim)
                .map(part -> parseSequence(ruleSpecs, rules, part, ruleName))
                .toArray(Sequence[]::new);
            var rule = new Either(ruleName, definitions[0], definitions[1]);
            rules.put(index, rule);
            return rule;
        } else {
            var rule = parseSequence(ruleSpecs, rules, definition, ruleName);
            rules.put(index, rule);
            return rule;
        }
    }

    private static Sequence parseSequence(Map<Integer, String> ruleSpecs, HashMap<Integer, Rule> rules,
                                          String definition,
                                          int ruleName) {
        var sequence = new ArrayList<Rule>();
        for (String s : definition.split("\\s")) {
            int i = Integer.parseInt(s);
            if (i == ruleName) {
                sequence.add(null);
            } else {
                Rule rule = parseRule(ruleSpecs, rules, i);
                sequence.add(rule);
            }
        }
        var result = new Sequence(ruleName, sequence);
        sequence.replaceAll(rule -> rule == null ? result : rule);
        return result;
    }

    sealed interface Result {
        boolean isValid();
    }

    record Invalid() implements Result {
        @Override
        public boolean isValid() {
            return false;
        }
    }

    record Valid(String remainder) implements Result {
        @Override
        public boolean isValid() {
            return true;
        }
    }

    sealed interface Rule {
        int rule();

        Result match(String message);

        String toRegex();
    }

    record Literal(int rule, String string) implements Rule {
        @Override
        public Result match(String message) {
            if (message.startsWith(string)) {
                return new Valid(message.substring(string.length()));
            }
            return new Invalid();
        }

        @Override
        public String toRegex() {
            return string;
        }

        @Override
        public String toString() {
            return Integer.toString(rule);
        }
    }

    record Sequence(int rule, List<Rule> rules) implements Rule {
        @Override
        public Result match(String message) {
            var input = message;
            for (var rule : rules) {
                var result = rule.match(input);
                if (result instanceof Valid valid) {
                    input = valid.remainder;
                } else {
                    return result;
                }
            }
            return new Valid(input);
        }

        @Override
        public String toRegex() {
            var builder = new StringBuilder("(");

            String beforeRecursive = null;
            for (int i = 0; i < rules.size(); i++) {
                var current = rules.get(i);
                if (current != this) {
                    builder.append(current.toRegex());
                } else if (i == rules.size() - 1) {
                    builder.append(")+");
                    return builder.toString();
                } else {
                    beforeRecursive = builder.toString();
                }
            }
            if (beforeRecursive != null) {
                var after = builder.substring(beforeRecursive.length());
                // ignore first opening
                beforeRecursive = beforeRecursive.substring(1);
                for (int i = 1; i < 5; i++) {
                    builder.append("|")
                        .append(beforeRecursive.repeat(i + 1))
                        .append(after.repeat(i + 1));
                }
            }

            builder.append(")");
            return builder.toString();
        }

        @Override
        public String toString() {
            return Integer.toString(rule);
        }
    }

    record Either(int rule, Rule left, Rule right) implements Rule {
        @Override
        public Result match(String message) {
            var result = left.match(message);
            if (result.isValid()) {
                return result;
            }
            return right.match(message);
        }

        @Override
        public String toRegex() {
            return "(" + left.toRegex() + "|" + right.toRegex() + ")";
        }

        @Override
        public String toString() {
            return Integer.toString(rule);
        }
    }
}
