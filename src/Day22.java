import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day22 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input22.txt"))
            .split("\n\n");

        var player1 = new ArrayDeque<>(parseCards(input[0]));
        var player2 = new ArrayDeque<>(parseCards(input[1]));

        part01(player1, player2);
        part02(player1, player2);
    }

    private static void part01(Queue<Integer> player1, Queue<Integer> player2) {
        player1 = new ArrayDeque<>(player1);
        player2 = new ArrayDeque<>(player2);

        while (!player1.isEmpty() && !player2.isEmpty()) {
            var card1 = player1.poll();
            var card2 = player2.poll();

            //noinspection ConstantConditions
            if (card1 < card2) {
                player2.add(card2);
                player2.add(card1);
            } else {
                player1.add(card1);
                player1.add(card2);
            }
        }
        System.out.println(calculateScore(player1.isEmpty() ? player2 : player1));
    }

    private static void part02(Queue<Integer> player1, Queue<Integer> player2) {
        player1 = new LinkedList<>(player1);
        player2 = new LinkedList<>(player2);

        var winner = recursiveGame(player1, player2);

        System.out.println(calculateScore(winner.winningQueue));
    }

    private static GameResult recursiveGame(Queue<Integer> player1, Queue<Integer> player2) {
        var history = new HashSet<Integer>();

        while (!player1.isEmpty() && !player2.isEmpty()) {
            if (!history.add(player1.hashCode() * 31 + player2.hashCode())) {
                return new GameResult(false, player1);
            }

            var card1 = player1.poll();
            var card2 = player2.poll();

            //noinspection ConstantConditions
            if (player1.size() >= card1 && player2.size() >= card2) {
                var new1 = player1.stream().limit(card1).collect(Collectors.toCollection(LinkedList::new));
                var new2 = player2.stream().limit(card2).collect(Collectors.toCollection(LinkedList::new));
                var result = recursiveGame(new1, new2);
                if (result.winningPlayer) {
                    player2.add(card2);
                    player2.add(card1);
                } else {
                    player1.add(card1);
                    player1.add(card2);
                }
                continue;
            }

            //noinspection ConstantConditions
            if (card1 < card2) {
                player2.add(card2);
                player2.add(card1);
            } else {
                player1.add(card1);
                player1.add(card2);
            }
        }
        if (player1.isEmpty()) {
            return new GameResult(true, player2);
        } else {
            return new GameResult(false, player1);
        }
    }

    private static List<Integer> parseCards(String input) {
        return Arrays.stream(input.split("\n"))
            .skip(1)
            .map(Integer::parseInt)
            .collect(Collectors.toList());
    }

    private static int calculateScore(Queue<Integer> cards) {
        var weight = cards.size();
        var sum = 0;

        while (!cards.isEmpty()) {
            sum += cards.poll() * weight;
            weight--;
        }
        return sum;
    }

    record GameResult(boolean winningPlayer, Queue<Integer> winningQueue) {
    }
}
