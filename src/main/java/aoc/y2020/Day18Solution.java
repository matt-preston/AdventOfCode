package aoc.y2020;

import com.google.common.base.Preconditions;
import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;


@AdventOfCode(year = 2020, day = 18, name = "Operation Order")
public class Day18Solution {

    private static final String MOCK = """
            1 + 2 * 3 + 4 * 5 + 6
            """;

    private static final String MOCK2 = """
            ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(71, solve(mockInput(MOCK)));
        assertEquals(13632, solve(mockInput(MOCK2)));
    }

    @Test
    public void part1() {
        assertEquals(1402255785165L, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(231, solve2(mockInput(MOCK)));
        assertEquals(23340, solve2(mockInput(MOCK2)));
    }

    @Test
    public void part2() {
        assertEquals(119224703255966L, solve2(input(this)));
    }

    private long solve(Input input) {
        return input.lines().stream()
                .mapToLong(line -> new Part1Parser(line).expression())
                .sum();
    }

    private long solve2(Input input) {
        return input.lines().stream()
                .mapToLong(line -> new Part2Parser(line).expression())
                .sum();
    }

    enum TokenType {
        EOF, ADD, MUL, LPAREN, RPAREN, DIGIT
    }

    record Token(TokenType type, char value) {}

    public static class Tokenizer {
        private final String text;
        private int position;
        private char current;

        public Tokenizer(String text) {
            this.text = text;
            this.position = 0;
            this.current = text.charAt(position);
        }

        public Token nextToken() {
            while(current != (char) -1) {
                if (Character.isWhitespace(current)) {
                    advance();
                } else if (Character.isDigit(current)) {
                    var t = new Token(TokenType.DIGIT, current);
                    advance();
                    return t;
                } else if (current == '(') {
                    advance();
                    return new Token(TokenType.LPAREN, '(');
                } else if (current == ')') {
                    advance();
                    return new Token(TokenType.RPAREN, ')');
                } else if (current == '*') {
                    advance();
                    return new Token(TokenType.MUL, '*');
                } else if (current == '+') {
                    advance();
                    return new Token(TokenType.ADD, '+');
                }
            }

            return new Token(TokenType.EOF, current);
        }

        private void advance() {
            if (++position < text.length()) {
                current = text.charAt(position);
            } else {
                current =  (char) -1;
            }
        }
    }

    /*
     * left to right operator order
     * ----------------------------
     *
     * expression = factor { (addop | mulop) factor }
     * factor = "(" expression ")" | digit
     * addop = "+"
     * mulop = "*"
     * digit = "0"|"1"|"2"|"3"|"4"|"5"|"6"|"7"|"8"|"9";
     */
    static class Part1Parser {
        private final Tokenizer tokenizer;
        private Token current;

        public Part1Parser(String text) {
            this.tokenizer = new Tokenizer(text);
            this.current = tokenizer.nextToken();
        }

        private void eat(TokenType type) {
            Preconditions.checkState(current.type() == type);
            this.current = tokenizer.nextToken();
        }

        private long expression() {
            var result = factor();
            while(current.type() == TokenType.MUL || current.type() == TokenType.ADD) {
                if (current.type() == TokenType.MUL) {
                    eat(TokenType.MUL);
                    result *= factor();
                } else {
                    eat(TokenType.ADD);
                    result += factor();
                }
            }
            return result;
        }

        private long factor() {
            if (current.type() == TokenType.LPAREN) {
                eat(TokenType.LPAREN);
                var result = expression();
                eat(TokenType.RPAREN);
                return result;
            } else if (current.type() == TokenType.DIGIT) {
                var result = current.value() - '0';
                eat(TokenType.DIGIT);
                return result;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    /*
     * operator order: + then *
     * ------------------------
     *
     * expression = term { mulop term }
     * term = factor { addop factor }
     * factor = "(" expression ")" | digit
     * addop = "+"
     * mulop = "*"
     * digit = "0"|"1"|"2"|"3"|"4"|"5"|"6"|"7"|"8"|"9";
     */
    static class Part2Parser {
        private final Tokenizer tokenizer;
        private Token current;

        public Part2Parser(String text) {
            this.tokenizer = new Tokenizer(text);
            this.current = tokenizer.nextToken();
        }

        private void eat(TokenType type) {
            Preconditions.checkState(current.type() == type);
            this.current = tokenizer.nextToken();
        }

        private long expression() {
            var result = term();
            while(current.type() == TokenType.MUL) {
                eat(TokenType.MUL);
                result *= term();
            }
            return result;
        }

        private long term() {
            var result = factor();
            while(current.type() == TokenType.ADD) {
                eat(TokenType.ADD);
                result += factor();
            }
            return result;
        }

        private long factor() {
            if (current.type() == TokenType.LPAREN) {
                eat(TokenType.LPAREN);
                var result = expression();
                eat(TokenType.RPAREN);
                return result;
            } else if (current.type() == TokenType.DIGIT) {
                var result = current.value() - '0';
                eat(TokenType.DIGIT);
                return result;
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
