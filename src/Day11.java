import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Day11 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input11.txt"));

        var grid = input.stream()
            .map(String::toCharArray)
            .toArray(char[][]::new);

        part01(grid);
        part02(grid);
    }

    private static void part01(char[][] grid) {
        gameOfLifeCommon(grid, Day11::countOccupiedPart01, 4);
    }

    private static void part02(char[][] grid) {
        gameOfLifeCommon(grid, Day11::countOccupiedPart02, 5);
    }

    private static void gameOfLifeCommon(char[][] grid, Counter counter, int maxPeople) {
        char[][] previous;
        while (true) {
            previous = grid;
            grid = gameOfLifeStep(grid, counter, maxPeople);
            if (Arrays.deepEquals(previous, grid)) {
                System.out.println(countAllOccupied(grid));
                break;
            }
        }
    }

    private static int countAllOccupied(char[][] grid) {
        var occupied = 0;
        for (char[] chars : grid) {
            for (char aChar : chars) {
                if (aChar == '#') occupied++;
            }
        }
        return occupied;
    }

    private static char[][] gameOfLifeStep(char[][] grid, Counter counter, int maxPeople) {
        var newGrid = new char[grid.length][grid[0].length];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                switch (grid[i][j]) {
                    case 'L':
                        if (counter.countOccupied(grid, i, j) == 0) {
                            newGrid[i][j] = '#';
                        } else {
                            newGrid[i][j] = 'L';
                        }
                        break;
                    case '#':
                        if (counter.countOccupied(grid, i, j) >= maxPeople) {
                            newGrid[i][j] = 'L';
                        } else {
                            newGrid[i][j] = '#';
                        }
                        break;
                    case '.':
                        newGrid[i][j] = '.';
                        break;
                }
            }
        }

        return newGrid;
    }

    private static int countOccupiedPart01(char[][] grid, int y, int x) {
        var occupied = 0;
        for (int i = Math.max(0, y - 1); i <= Math.min(y + 1, grid.length - 1); i++) {
            for (int j = Math.max(0, x - 1); j <= Math.min(x + 1, grid[i].length - 1); j++) {
                if (i == y && j == x) continue;

                if (grid[i][j] == '#') occupied++;
            }
        }
        return occupied;
    }

    private static int countOccupiedPart02(char[][] grid, int y, int x) {
        int count = 0;
        // Top left
        int i = y - 1;
        int j = x - 1;
        while (i >= 0 && j >= 0) {
            if (grid[i][j] != '.') {
                if (grid[i][j] == '#') count++;
                break;
            }
            i--;
            j--;
        }
        // Top
        i = y - 1;
        while (i >= 0) {
            if (grid[i][x] != '.') {
                if (grid[i][x] == '#') count++;
                break;
            }
            i--;
        }
        // Top right
        i = y - 1;
        j = x + 1;
        while (i >= 0 && j < grid[i].length) {
            if (grid[i][j] != '.') {
                if (grid[i][j] == '#') count++;
                break;
            }
            i--;
            j++;
        }
        // Left
        j = x - 1;
        while (j >= 0) {
            if (grid[y][j] != '.') {
                if (grid[y][j] == '#') count++;
                break;
            }
            j--;
        }
        // Right
        j = x + 1;
        while (j < grid[0].length) {
            if (grid[y][j] != '.') {
                if (grid[y][j] == '#') count++;
                break;
            }
            j++;
        }
        // Bottom left
        i = y + 1;
        j = x - 1;
        while (i < grid.length && j >= 0) {
            if (grid[i][j] != '.') {
                if (grid[i][j] == '#') count++;
                break;
            }
            i++;
            j--;
        }
        // Bottom
        i = y + 1;
        while (i < grid.length) {
            if (grid[i][x] != '.') {
                if (grid[i][x] == '#') count++;
                break;
            }
            i++;
        }
        // Bottom right
        i = y + 1;
        j = x + 1;
        while (i < grid.length && j < grid[i].length) {
            if (grid[i][j] != '.') {
                if (grid[i][j] == '#') count++;
                break;
            }
            i++;
            j++;
        }
        return count;
    }

    @FunctionalInterface
    interface Counter {
        int countOccupied(char[][] grid, int y, int x);
    }
}
