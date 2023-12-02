import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.StringTemplate.STR;

Map<String, String> strToDigit = Map.of(
        "one", "1",
        "two", "2",
        "three", "3",
        "four", "4",
        "five", "5",
        "six", "6",
        "seven", "7",
        "eight", "8",
        "nine", "9"
);

String regex = String.join("|", strToDigit.keySet()) + "|" +
               String.join("|", strToDigit.values());

final Pattern pattern = Pattern.compile(STR."(?<first>\{regex}).*(?<last>\{regex})|(?<alone>\\d)");

void main(String[] args) throws IOException {
    System.out.println(Files.lines(Paths.get("D:\\input.txt"))
            .mapToInt(this::extractNumber)
            .sum());
}

int extractNumber(String line) {
    final Matcher matcher = pattern.matcher(line);
    matcher.find();
    String alone = matcher.group("alone");
    if (alone == null) {
        String firstDigit = convertToDigit(matcher.group("first"));
        String secondDigit = convertToDigit(matcher.group("last"));
        return parseInt(firstDigit + secondDigit);
    }
    return parseInt(alone + alone);
}

String convertToDigit(String str) {
    return str.length() == 1 ? str : strToDigit.get(str);
}