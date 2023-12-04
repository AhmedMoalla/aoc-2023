import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.Files.readAllLines;
import static java.nio.file.Paths.get;

void main() throws IOException {
    List<String> lines = readAllLines(get("D:\\input.txt"));

    int[] nbCopies = new int[lines.size()];
    for (int i = 0; i < lines.size(); i++) {
        nbCopies[i]++;
        String[] split = lines.get(i).split(":")[1].split("\\|");
        int nbMatching = Sets.intersection(toSet(split[0]), toSet(split[1])).size();
        for (int copy = 0; copy < nbMatching; copy++) {
            nbCopies[i + copy + 1]++;
        }
        for (int copy = 0; copy < nbCopies[i] - 1; copy++) {
            for (int copyOfCopy = 1; copyOfCopy <= nbMatching; copyOfCopy++) {
                nbCopies[i + copyOfCopy]++;
            }
        }
    }

    System.out.println(Arrays.stream(nbCopies).sum());
}

Set<String> toSet(String str) {
    return Arrays.stream(str.split(" "))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .collect(Collectors.toSet());
}