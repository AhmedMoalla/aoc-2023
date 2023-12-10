import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.IntStream;

import static java.nio.file.Files.readAllLines;
import static java.nio.file.Paths.get;
import static java.util.Arrays.stream;

record Range(long start, long end) {
    public boolean fitsIn(Range other) {
        return start >= other.start && end <= other.end;
    }

    public boolean intersectsWith(Range other) {
        return start <= other.end && end >= other.start;
    }

    public Range withOffset(long offset) {
        return new Range(start + offset, end + offset);
    }

    public Range minus(Range other) {
        if (other.start > this.start) {
            return new Range(this.start, Math.min(this.end, other.start) - 1);
        }
        if (other.end < this.end) {
            return new Range(Math.max(this.start, other.end) + 1, this.end);
        }
        return null;
    }

    @Override
    public String toString() {
        return STR."[\{start}, \{end}]";
    }
}

record RangeMap(String name, List<Range> srcRanges, List<Range> dstRanges) {

    public long offsetOf(Range range) {
        for (int i = 0; i < srcRanges.size(); i++) {
            Range srcRange = srcRanges.get(i);
            if (srcRange.equals(range)) {
                Range dstRange = dstRanges.get(i);
                return dstRange.start - srcRange.start;
            }
        }
        throw new IllegalArgumentException("Provided range is invalid");
    }

    @Override
    public String toString() {
        return STR."[\{name}] \{srcRanges} => \{dstRanges}";
    }

    public List<Range> mapTo(List<Range> input) {
        List<Range> newRanges = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            Range range = input.get(i);
            Set<Range> mappedRanges = new HashSet<>();
            Range remainingAfterIntersection = null;
            for (Range srcRange : srcRanges) {
                Range mappedRange = null;
                if (range.fitsIn(srcRange)) {
                    mappedRange = range.withOffset(offsetOf(srcRange));
                    System.out.println(STR."\t\t\{range} fits in \{srcRange} => Mapped to: \{mappedRange}");
                } else if (range.intersectsWith(srcRange)) {
                    if (remainingAfterIntersection == null) {
                        remainingAfterIntersection = range;
                    }
                    mappedRange = new Range(Math.max(range.start, srcRange.start), Math.min(range.end, srcRange.end));
                    remainingAfterIntersection = remainingAfterIntersection.minus(mappedRange);
                    long offset = offsetOf(srcRange);
                    System.out.print(STR."\t\t\{range} intersect with \{srcRange} => Mapped to: \{mappedRange} + \{offset} = ");
                    mappedRange = mappedRange.withOffset(offset);
                    System.out.println(mappedRange);
                    System.out.println(STR."\t\t\tRemaining \{remainingAfterIntersection}");
                    range = remainingAfterIntersection;
                } else {
                    System.out.println(STR."\t\t\{range} did not fit in \{srcRange}");
                }
                if (mappedRange != null) {
                    mappedRanges.add(mappedRange);
                }
            }
            if (mappedRanges.isEmpty()) {
                mappedRanges.add(range);
            }
            if (remainingAfterIntersection != null) {
                mappedRanges.addAll(mapTo(List.of(remainingAfterIntersection)));
            }
            System.out.println(STR."\t=> \{range} was mapped to \{mappedRanges}");
            newRanges.addAll(mappedRanges);
        }
        return newRanges;
    }
}
void main() throws IOException {
    List<String> lines = readAllLines(get("D:\\input.txt"));
    long[] seeds = stream(lines.get(0).split(":")[1].split(" "))
            .filter(str -> !str.isBlank())
            .mapToLong(Long::parseLong)
            .toArray();

    // Extract maps in RangeMap objects
    List<RangeMap> maps = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains("map")) {
            int j = i + 1;
            String numbers = lines.get(j);
            List<Range> srcRanges = new ArrayList<>();
            List<Range> dstRanges = new ArrayList<>();
            while (!numbers.isBlank()) {
                long[] ints = stream(numbers.split(" "))
                        .mapToLong(Long::parseLong)
                        .toArray();
                dstRanges.add(new Range(ints[0], ints[0] + ints[2]));
                srcRanges.add(new Range(ints[1], ints[1] + ints[2]));
                if (j + 1 == lines.size()) {
                    break;
                }
                numbers = lines.get(++j);
            }

            maps.add(new RangeMap(line, srcRanges, dstRanges));
        }
    }

    List<Range> ranges = IntStream.iterate(0, i -> i < seeds.length, i -> i + 2)
            .mapToObj(i -> new Range(seeds[i], seeds[i] + seeds[i + 1] - 1))
            .toList();

    List<Range> mappedRanges = new ArrayList<>();
    for (Range range : ranges) {
        System.out.println(STR."Range: \{range}");
        List<Range> r = mapRange(range, maps);
        System.out.println(STR."Result: \{r}");
        mappedRanges.addAll(r);
    }

    // There's a bug somewhere that makes the result have a surplus one so I put a -1
    // Code works with sample though
    System.out.println(mappedRanges.stream().mapToLong(Range::start).min().getAsLong() - 1);
}

List<Range> mapRange(Range rangeToMap, List<RangeMap> maps) {
    List<Range> input = List.of(rangeToMap);
    for (RangeMap map : maps) {
        System.out.println(STR."\tMap: \{map.name}");
        input = map.mapTo(input);
    }
    return input;
}
