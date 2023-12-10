import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.util.Arrays.stream;

void main() throws IOException {
    List<String> lines = lines(get("D:\\input.txt"))
            .filter(line -> !line.isBlank())
            .toList();
    long[] seeds = stream(lines.getFirst().split(":")[1].split(" "))
            .filter(str -> !str.isBlank())
            .mapToLong(Long::parseLong)
            .toArray();

    Set<Integer> seedsToMap = null;
    for (int i = 1; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains("map")) {
            seedsToMap = IntStream.range(0, seeds.length).boxed().collect(Collectors.toSet());
        } else {
            long[] ints = stream(line.split(" "))
                    .mapToLong(Long::parseLong)
                    .toArray();
            long dstStart = ints[0], srcStart = ints[1], length = ints[2];
            for (int k = 0; k < seeds.length; k++) {
                if (seeds[k] >= srcStart && seeds[k] <= srcStart + length && seedsToMap.contains(k)) {
                    seeds[k] = dstStart + (seeds[k] - srcStart);
                    seedsToMap.remove(k);
                }
            }
        }
    }
    System.out.println(stream(seeds).min().getAsLong());
}
