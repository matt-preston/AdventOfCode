package aoc.y2023;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 4, name = "Scratchcards")
public class Day04Solution {

    private static final Pattern DIGITS = Pattern.compile("\\d+");

    private static final String MOCK = """
            Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
            Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
            Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
            Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
            Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
            Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
            """;

    record Card(int cardNumber, Set<Integer> ourNumbers, Set<Integer> winningNumbers) {

        public int numMatches() {
            return Sets.intersection(ourNumbers, winningNumbers).size();
        }

        public int score() {
            return (int) Math.pow(2, numMatches() - 1);
        }

        public void addWinningCopies(final List<Card> all, final List<Card> winningCopies) {
            for (final Card card : following(all)) {
                winningCopies.add(card);
                card.addWinningCopies(all, winningCopies);
            }
        }

        private List<Card> following(final List<Card> allCards) {
            if (cardNumber >= allCards.size()) {
                return List.of();
            } else {
                return allCards.subList(cardNumber, Math.min(cardNumber + numMatches(), allCards.size()));
            }
        }
    }

    @Test
    public void part1WithMockData() {
        assertEquals(13, solutionPart1(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(21558, solutionPart1(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(30, solutionPart2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(10425665, solutionPart2(input(this)));
    }

    private int solutionPart1(final Input input) {
        return cards(input).stream().mapToInt(Card::score).sum();
    }

    private int solutionPart2(final Input input) {
        var cards = cards(input);
        var copies = Lists.<Card>newArrayList();

        for (final Card card : cards(input)) {
            copies.add(card);
            card.addWinningCopies(cards, copies);
        }

        return copies.size();
    }

    private List<Card> cards(final Input input) {
        var cards = Lists.<Card>newArrayList();
        var index = 1;
        for (final String line : input.lines()) {
            var parts = line.split(":", 2)[1].split("\\|", 2);
            var card = new Card(index++, numbers(parts[0]), numbers(parts[1]));
            cards.add(card);
        }
        return cards;
    }

    private Set<Integer> numbers(final String input) {
        return DIGITS.matcher(input).results()
                .map(m -> parseInt(m.group()))
                .collect(toCollection(LinkedHashSet::new));
    }
}
