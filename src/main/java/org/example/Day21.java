import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;

final Map<String, Integer> MAX = Map.of(
        "red", 12,
        "green", 13,
        "blue", 14
);

void main() throws IOException {
    System.out.println(Files.lines(Paths.get("D:\\input.txt"))
            .filter(this::isPossible)
            .mapToInt(this::extractGameId)
            .sum());
}

boolean isPossible(String line) {
    return stream(line.split(":")[1].split(";"))
            .flatMap(set -> stream(set.split(",")))
            .map(String::trim)
            .map(s -> s.split("\\s"))
            .allMatch(s -> {
                int number = parseInt(s[0]);
                String color = s[1];
                return number <= MAX.get(color);
            });
}

int extractGameId(String line) {
    return parseInt(line.split(":")[0].split("\\s")[1]);
}