import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;

void main() throws IOException {
    int sum = lines(get("D:\\input.txt"))
            .map(line -> line.split(":")[1].split("\\|"))
            .map(strs -> new Set[]{toSet(strs[0]), toSet(strs[1])})
            .map(sets -> Sets.intersection(sets[0], sets[1]))
            .filter(inter -> !inter.isEmpty())
            .mapToInt(inter -> (int) Math.pow(2, inter.size() - 1))
            .sum();
    System.out.println(sum);
}

Set<String> toSet(String str) {
    return Arrays.stream(str.split(" "))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .collect(Collectors.toSet());
}