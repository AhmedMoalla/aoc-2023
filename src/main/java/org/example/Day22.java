import java.io.IOException;

import static java.lang.Integer.parseInt;
import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;

void main() throws IOException {
    System.out.println(lines(get("D:\\input.txt"))
            .mapToInt(this::powerOfMinimumSet)
            .sum());
}

int powerOfMinimumSet(String line) {
    return stream(line.split(":")[1].split(";"))
            .flatMap(set -> stream(set.split(",")))
            .map(String::trim)
            .map(s -> s.split("\\s"))
            .collect(groupingBy(set -> set[1],
                    mapping(set -> parseInt(set[0]), reducing(0, Integer::max))))
            .values()
            .stream().mapToInt(Integer::intValue)
            .reduce(1, (i1, i2) -> i1 * i2);
}