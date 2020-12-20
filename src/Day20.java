import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day20 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input20.txt"))
            .split("\n\n");

        var tiles = Arrays.stream(input)
            .map(Tile::parse)
            .collect(Collectors.toList());

        part01(tiles);
        part02(tiles);
    }

    private static void part01(List<Tile> tiles) {
        var result = findUniqueEdges(tiles).stream()
            .filter(tile -> tile.edges.size() > 2)
            .mapToLong(Tile::id)
            .reduce(1, (accumulator, factor) -> accumulator * factor);
        System.out.println(result);
    }

    private static void part02(List<Tile> tiles) {
        var gridSize = (int) Math.sqrt(tiles.size());
        var grid = new Tile[tiles.size()];

        assemble(grid, gridSize, tiles);

        var allLines = new ArrayList<String>();
        for (int i = 0; i < gridSize; i++) {
            var lines = new String[8];
            for (int j = 0; j < gridSize; j++) {
                for (int k = 0; k < lines.length; k++) {
                    if (lines[k] == null) lines[k] = "";

                    lines[k] += grid[i * gridSize + j].content[k];
                }
            }
            allLines.addAll(Arrays.asList(lines));
        }
        search(allLines);
    }

    private static void assemble(Tile[] grid, int gridSize, List<Tile> tiles) {
        var tileMap = tiles.stream()
            .collect(Collectors.toMap(Tile::id, tile -> tile));
        var clippedTiles = findUniqueEdges(tiles);
        var corners = clippedTiles.stream()
            .filter(tile -> tile.edges.size() > 2)
            .collect(Collectors.toList());
        var topLeft = corners.stream()
            .findFirst()
            .orElseThrow();
        grid[0] = tileMap.get(topLeft.id);
        grid[0].alignTopLeft(topLeft.edges);
        tiles.remove(grid[0]);

        for (int i = 1; i < gridSize; i++) {
            grid[i] = tiles.stream()
                .filter(grid[i - 1]::isToRight)
                .findFirst()
                .orElseThrow();
            grid[i].alignLeft(grid[i - 1]);
            tiles.remove(grid[i]);
        }
        for (int i = 1; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Tile top = grid[(i - 1) * gridSize + j];
                grid[i * gridSize + j] = tiles.stream()
                    .filter(top::isToTop)
                    .findFirst()
                    .orElseThrow();
                grid[i * gridSize + j].alignTop(top);
                tiles.remove(grid[i * gridSize + j]);
            }
        }
    }

    private static void search(List<String> allLines) {
        var query = Arrays.stream("""
                              #\s
            #    ##    ##    ###
             #  #  #  #  #  #  \s
            """.split("\n"))
            .map(String::toCharArray)
            .toArray(char[][]::new);

        var data = new char[allLines.size()][];
        for (var i = 0; i < data.length; i++) {
            data[i] = allLines.get(i).toCharArray();
        }

        var foundMonster = false;
        var rotations = 0;
        do {
            for (int i = 0; i <= data.length - query.length; i++) {
                for (int j = 0; j <= data[i].length - query[0].length; j++) {
                    foundMonster |= findAndMark(data, query, i, j);
                }
            }
            if (foundMonster) {
                break;
            }
            data = rotate(data);
            rotations++;
            if (rotations == 4) {
                rotations = 0;
                data = flip(data);
            }
        } while (true);

        // Count
        var sum = 0;
        for (char[] datum : data) {
            for (char c : datum) {
                if (c == '#') sum++;
            }
        }
        System.out.println(sum);
    }

    private static char[][] rotate(char[][] data) {
        return applyCopy(data, (output, d, y, x) -> output[x][output.length - 1 - y] = d[y][x]);
    }

    private static char[][] flip(char[][] data) {
        return applyCopy(data, (output, d, y, x) -> output[y][data.length - 1 - x] = data[y][x]);
    }

    private static char[][] applyCopy(char[][] data, ArrayReindexer reindexer) {
        var output = new char[data[0].length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                reindexer.apply(output, data, i, j);
            }
        }
        return output;
    }

    private static boolean findAndMark(char[][] data, char[][] query, int y, int x) {
        for (int i = 0; i < query.length; i++) {
            for (int j = 0; j < query[i].length; j++) {
                if (query[i][j] != '#') continue;

                if (data[y + i][x + j] != query[i][j]) return false;
            }
        }
        // mark
        for (int i = 0; i < query.length; i++) {
            for (int j = 0; j < query[i].length; j++) {
                if (query[i][j] != '#') continue;

                data[y + i][x + j] = 'O';
            }
        }
        return true;
    }

    private static List<Tile> findUniqueEdges(List<Tile> tiles) {
        tiles = tiles.stream()
            .map(tile -> new Tile(tile.id, new ArrayList<>(tile.edges), null))
            .collect(Collectors.toList());

        for (int i = 0; i < tiles.size() - 1; i++) {
            for (int j = i + 1; j < tiles.size(); j++) {
                var first = tiles.get(i);
                var second = tiles.get(j);
                var clone = new ArrayList<>(first.edges);
                first.edges.removeAll(second.edges);
                second.edges.removeAll(clone);
            }
        }
        return tiles;
    }

    record Tile(int id, List<String> edges, String[] content) {
        static Tile parse(String input) {
            var lines = input.split("\n");
            var id = Integer.parseInt(lines[0].split("\s")[1].replaceAll(":", ""));

            var top = lines[1];
            var bottom = lines[lines.length - 1];
            var left = new StringBuilder();
            var right = new StringBuilder();
            var content = new String[lines.length - 3];
            for (int i = 1; i < lines.length; i++) {
                left.append(lines[i].charAt(0));
                right.append(lines[i].charAt(lines[i].length() - 1));
                if (i > 1 && i < lines.length - 1) {
                    content[i - 2] = lines[i].substring(1, lines[i].length() - 1);
                }
            }
            var permutations = new ArrayList<String>();
            permutations.add(left.toString());
            permutations.add(0, left.reverse().toString());
            permutations.add(top);
            permutations.add(new StringBuilder(top).reverse().toString());
            permutations.add(right.toString());
            permutations.add(right.reverse().toString());
            permutations.add(new StringBuilder(bottom).reverse().toString());
            permutations.add(bottom);

            return new Tile(id, permutations, content);
        }

        public void alignTopLeft(List<String> edges) {
            var indices = edges.stream()
                .mapToInt(this.edges::indexOf)
                .toArray();

            while (!Arrays.stream(indices).boxed().collect(Collectors.toList()).containsAll(List.of(0, 2))) {
                rotate();
                rotateArray(indices, content.length);
            }
        }

        public void alignLeft(Tile tile) {
            var rightEdges = List.of(tile.edges.get(4), tile.edges.get(5));
            var indices = rightEdges.stream()
                .mapToInt(edges::indexOf)
                .filter(i -> i != -1)
                .toArray();

            if (indices[0] == 1 && indices[1] == 0) return;

            flipIfNeeded(indices);
            while (indices[0] != 1 && indices[1] != 0) {
                rotate();
                rotateArray(indices, edges.size());
            }
        }

        public void alignTop(Tile tile) {
            var topEdges = List.of(tile.edges.get(6), tile.edges.get(7));
            var indices = topEdges.stream()
                .mapToInt(edges::indexOf)
                .filter(i -> i != -1)
                .toArray();

            if (indices[0] == 3 && indices[1] == 2) return;

            flipIfNeeded(indices);
            while (indices[0] != 3 && indices[1] != 2) {
                rotate();
                rotateArray(indices, edges.size());
            }
        }

        private void flipIfNeeded(int[] indices) {
            if (indices[1] > indices[0]) {
                flip();
                var tmp = indices[0];
                indices[0] = indices[1];
                indices[1] = tmp;
                if (indices[0] < 2 || indices[0] > 3 && indices[0] < 6) {
                    indices[0] += edges.size() / 2;
                    indices[1] += edges.size() / 2;
                    indices[0] %= edges.size();
                    indices[1] %= edges.size();
                }
            }
        }

        private void rotate() {
            var output = new char[content[0].length()][content.length];

            for (int i = 0; i < content.length; i++) {
                for (int j = 0; j < content[i].length(); j++) {
                    output[j][content[i].length() - 1 - i] = content[i].charAt(j);
                }
            }
            var rotated = Arrays.stream(output)
                .map(String::new)
                .toArray(String[]::new);
            System.arraycopy(rotated, 0, content, 0, rotated.length);
            edges.add(0, edges.remove(edges.size() - 1));
            edges.add(0, edges.remove(edges.size() - 1));
        }

        private void flip() {
            for (int i = 0; i < content.length; i++) {
                content[i] = new StringBuilder(content[i]).reverse().toString();
            }
            swap(0, 5);
            swap(1, 4);
            swap(2, 3);
            swap(6, 7);
        }

        public boolean isToRight(Tile tile) {
            var constraints = List.of(edges.get(4), edges.get(5));
            return tile.edges.containsAll(constraints);
        }

        public boolean isToTop(Tile tile) {
            var constraints = List.of(edges.get(6), edges.get(7));
            return tile.edges.containsAll(constraints);
        }

        private void swap(int first, int second) {
            var tmp = edges.get(second);
            edges.set(second, edges.get(first));
            edges.set(first, tmp);
        }
    }

    static void rotateArray(int[] array, int max) {
        for (int i = 0; i < array.length; i++) {
            array[i] += 2;
            array[i] %= max;
        }
    }

    interface ArrayReindexer {
        void apply(char[][] output, char[][] data, int y, int x);
    }
}
