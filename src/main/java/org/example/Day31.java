import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.*;

static final Pattern numbersOrSymbols = Pattern.compile("(?<number>\\d+)|([^A-Za-z0-9.])");

record Index(int start, int end) { }
record LineInfo(String line, List<Index> numberIndices, List<Index> symbolIndices) {
    static LineInfo fromLine(String line) {
        Matcher matcher = numbersOrSymbols.matcher(line);
        Map<Boolean, List<Index>> numbersAndSymbols = matcher.results()
                .collect(partitioningBy(match -> match.group("number") == null,
                        mapping(match -> new Index(match.start(), match.end()), toList())));
        return new LineInfo(line, numbersAndSymbols.get(false), numbersAndSymbols.get(true));
    }

    public int sumAdjacentNumbers(LineInfo other) {
        int sum = 0;
        for (Index sIndex : symbolIndices) {
            int index = sIndex.start;
            for (Index nIndex : other.numberIndices) {
                if (index >= nIndex.start - 1 && index <= nIndex.end) {
                    String number = other.line.substring(nIndex.start, nIndex.end);
                    sum += parseInt(number);
                }
            }
        }
        return sum;
    }
}

void main() throws IOException {
    List<LineInfo> infos = lines(get("D:\\input.txt"))
            .map(LineInfo::fromLine)
            .toList();

    int sum = 0;
    for (int i = 0; i < infos.size(); i++) {
        LineInfo previous = i - 1 < 0 ? null : infos.get(i - 1);
        LineInfo current = infos.get(i);
        LineInfo next = i + 1 >= infos.size() ? null : infos.get(i + 1);
        sum += current.sumAdjacentNumbers(previous);
        sum += current.sumAdjacentNumbers(current);
        sum += current.sumAdjacentNumbers(next);
    }

    System.out.println(sum);
}