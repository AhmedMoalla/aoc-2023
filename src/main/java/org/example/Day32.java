import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.nio.file.Files.lines;
import static java.util.stream.Collectors.*;

static final Pattern numbersOrStars = Pattern.compile("(?<number>\\d+)|(\\*)");

record Index(int start, int end) { }
record LineInfo(String line, List<Index> numberIndices, List<Index> starIndices) {
    static LineInfo fromLine(String line) {
        Matcher matcher = numbersOrStars.matcher(line);
        Map<Boolean, List<Index>> numbersAndSymbols = matcher.results()
                .collect(partitioningBy(match -> match.group("number") == null,
                        mapping(match -> new Index(match.start(), match.end()), toList())));
        return new LineInfo(line, numbersAndSymbols.get(false), numbersAndSymbols.get(true));
    }

    Multimap<Integer, Integer> adjacentNumbersPerStar(LineInfo other) {
        Multimap<Integer, Integer> numsPerStar = HashMultimap.create();
        if (other == null) {
            return numsPerStar;
        }

        for (Index sIndex : starIndices) {
            int index = sIndex.start;
            for (Index nIndex : other.numberIndices) {
                if (index >= nIndex.start - 1 && index <= nIndex.end) {
                    String number = other.line.substring(nIndex.start, nIndex.end);
                    numsPerStar.put(index, parseInt(number));
                }
            }
        }
        return numsPerStar;
    }

    boolean hasStars() {
        return !starIndices.isEmpty();
    }
}

void main() throws IOException {
    List<LineInfo> infos = lines(Paths.get("D:\\input.txt"))
            .map(LineInfo::fromLine)
            .toList();

    int sum = 0;
    for (int i = 0; i < infos.size(); i++) {
        LineInfo previous = i - 1 < 0 ? null : infos.get(i - 1);
        LineInfo current = infos.get(i);
        LineInfo next = i + 1 >= infos.size() ? null : infos.get(i + 1);
        if (current.hasStars()) {
            var nums = current.adjacentNumbersPerStar(previous);
            nums.putAll(current.adjacentNumbersPerStar(current));
            nums.putAll(current.adjacentNumbersPerStar(next));
            sum += nums.asMap().values()
                    .stream().filter(ns -> ns.size() == 2)
                    .mapToInt(ns -> ns.stream().reduce((i1, i2) -> i1 * i2).get())
                    .sum();
        }
    }

    System.out.println(sum);
}