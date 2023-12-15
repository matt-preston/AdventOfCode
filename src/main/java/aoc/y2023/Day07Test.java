package aoc.y2023;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 7)
public class Day07Test {

  enum Card {
    JOKER, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
  }

  enum Type {
    HIGH_CARD, PAIR, TWO_PAIR, THREE_OF_KIND, FULL_HOUSE, FOUR_OF_KIND, FIVE_OK_KIND
  }

  record Hand(String raw, List<Card> cards, long bid) implements Comparable<Hand> {

    @Override
    public int compareTo(final Hand o) {
      return ComparisonChain.start()
          .compare(type(), o.type())
          .compare(cards.get(0), o.cards.get(0))
          .compare(cards.get(1), o.cards.get(1))
          .compare(cards.get(2), o.cards.get(2))
          .compare(cards.get(3), o.cards.get(3))
          .compare(cards.get(4), o.cards.get(4))
          .result();
    }

    public Type type() {
      var counts = Maps.<Card, Integer>newHashMap();
      for (final Card card : cards) {
        counts.compute(card, (k, v) -> (v == null) ? 1 : v + 1);
      }

      var jokers = counts.getOrDefault(Card.JOKER, 0);

      if (fiveOfKind(counts, jokers)) {
        return Type.FIVE_OK_KIND;
      } else if (fourOfKind(counts, jokers)) {
        return Type.FOUR_OF_KIND;
      } else if (fullHouse(counts, jokers)) {
        return Type.FULL_HOUSE;
      } else if (threeOfKind(counts, jokers)) {
        return Type.THREE_OF_KIND;
      } else if (counts.containsValue(2) && counts.size() == 3) {  // don't care about jokers
        return Type.TWO_PAIR;
      } else if (counts.containsValue(2) || jokers == 1) { // a pair and a joker would be 3 of a kind
        return Type.PAIR;
      } else {
        checkState(counts.size() == 5 && jokers == 0);
        return Type.HIGH_CARD;
      }
    }

    private boolean fiveOfKind(Map<Card, Integer> counts, int jokers) {
      return counts.containsValue(5)
             || (counts.containsValue(4) && jokers == 1)
             || (counts.containsValue(3) && jokers == 2)
             || (counts.containsValue(2) && jokers == 3)
             || jokers == 4;
    }

    private boolean fourOfKind(Map<Card, Integer> counts, int jokers) {
      return counts.containsValue(4)
             || (counts.containsValue(3) && jokers == 1)
             || (counts.containsValue(2) && jokers == 2 && counts.size() == 3)
             || jokers == 3;
    }

    private boolean fullHouse(Map<Card, Integer> counts, int jokers) {
      return (counts.containsValue(3) && counts.containsValue(2))
             || (counts.containsValue(2) && counts.size() == 3 && jokers == 1); // 2 pairs and a joker
    }

    private boolean threeOfKind(Map<Card, Integer> counts, int jokers) {
      return counts.containsValue(3)
             || (counts.containsValue(2) && jokers == 1)
             || jokers == 2;
    }
  }

  @Test
  public void part1WithMockData() {
    assertEquals(6440, solutionForPart1(mockInput("""
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
        """)));
  }

  @Test
  public void part1() {
    assertEquals(253638586, solutionForPart1(input(this)));
  }

  @Test
  public void part2WithMockData() {
    assertEquals(5905, solutionForPart2(mockInput("""
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
        """)));
  }

  @Test
  public void part2() {
    assertEquals(253253225, solutionForPart2(input(this)));
  }


  private long solutionForPart1(final Input input) {
    final var hands = Lists.<Hand>newArrayList();
    for (final String line : input.lines()) {
      hands.add(hand(line));
    }
    hands.sort(Comparator.naturalOrder());

    var total = 0L;
    for (int i = 0; i < hands.size(); i++) {
      total += ((i + 1) * hands.get(i).bid());
    }

    return total;
  }


  private long solutionForPart2(final Input input) {
    final var hands = Lists.<Hand>newArrayList();
    for (final String line : input.lines()) {
      // J is now a joker
      hands.add(hand(line.replaceAll("J", "X")));
    }
    hands.sort(Comparator.naturalOrder());

    var total = 0L;
    for (int i = 0; i < hands.size(); i++) {
      total += ((i + 1) * hands.get(i).bid());
    }

    return total;
  }

  private Hand hand(final String line) {
    var parts = line.split(" ", 2);
    List<Card> cards = cards(parts[0]);
    var bid = Long.parseLong(parts[1]);
    return new Hand(parts[0], cards, bid);
  }

  private List<Card> cards(final String cards) {
    var result = Lists.<Card>newArrayList();
    for (final char c : cards.toCharArray()) {
      result.add(card(c));
    }

    return result;
  }

  private Card card(final char c) {
    return switch (c) {
      case 'A' -> Card.ACE;
      case 'K' -> Card.KING;
      case 'Q' -> Card.QUEEN;
      case 'J' -> Card.JACK;
      case 'T' -> Card.TEN;
      case '9' -> Card.NINE;
      case '8' -> Card.EIGHT;
      case '7' -> Card.SEVEN;
      case '6' -> Card.SIX;
      case '5' -> Card.FIVE;
      case '4' -> Card.FOUR;
      case '3' -> Card.THREE;
      case '2' -> Card.TWO;
      default -> Card.JOKER; // joker
    };
  }
}
