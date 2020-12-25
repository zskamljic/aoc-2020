import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day25 {
    public static void main(String[] args) throws IOException {
        var values = Files.readAllLines(Paths.get("input25.txt"))
            .stream()
            .mapToLong(Long::parseLong)
            .toArray();

        var card = transform(values[0]);
        var key = getKey(values[1], card);
        System.out.println(key);
    }

    static long transform(long target) {
        var i = 0L;
        var transform = 1L;
        while (transform != target) {
            i++;
            transform = (transform * 7) % 20201227;
        }
        return i;
    }

    static long getKey(long subject, long loops) {
        var transform = 1L;
        for (int i = 0; i < loops; i++) {
            transform = (transform * subject) % 20201227;
        }
        return transform;
    }
}
