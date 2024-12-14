package aoc.y2024;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 14, name = "Restroom Redoubt")
public class Day14Solution {

    private static final String MOCK = """
            p=0,4 v=3,-3
            p=6,3 v=-1,-3
            p=10,3 v=-1,2
            p=2,0 v=2,-1
            p=0,0 v=1,3
            p=3,0 v=-2,-2
            p=7,6 v=-1,-3
            p=3,0 v=-1,-2
            p=9,3 v=2,3
            p=7,3 v=-1,2
            p=2,4 v=2,-3
            p=9,5 v=-3,-3
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(12, safetyFactor(mockInput(MOCK), 11, 7));
    }

    @Test
    public void part1() {
        assertEquals(214109808, safetyFactor(input(this), 101, 103));
    }

    @Test
    public void part2() throws Exception {
        var robots = parse(input(this));

        var width = 101;
        var height = 103;

        for (int i = 1; i < width * height; i++) {
            var seconds = i;
            var pixels = robots.stream()
                    .map(robot -> predict(robot.position(), robot.velocity(), width, height, seconds))
                    .collect(Collectors.toSet());

            var keep = false;

            // Check for pixels grouped together
            for (Vector2 p : pixels) {
                if (pixels.contains(p.north())
                        && pixels.contains(p.south())
                        && pixels.contains(p.south().east())
                        && pixels.contains(p.south().west())
                        && pixels.contains(p.east())
                        && pixels.contains(p.west())) {
                    keep = true;
                    break;
                }
            }

            if (keep) {
                var image = new BufferedImage(width + 1, height + 1, TYPE_INT_RGB);
                for (Vector2 position : pixels) {
                    image.setRGB(position.x(), position.y(), Color.WHITE.getRGB());
                    ImageIO.write(image, "PNG", new File(String.format("docs/2024/14/%05d.png", i)));
                }
            }
        }
    }

    private int safetyFactor(Input input, int width, int height) {
        int q1 = 0;
        int q2 = 0;
        int q3 = 0;
        int q4 = 0;

        for (Robot robot : parse(input)) {
            var position = predict(robot.position(), robot.velocity(), width, height, 100);

            if (position.x() < width / 2) {
                if (position.y() < height / 2) {
                    q1++;
                }
                if (position.y() > height / 2) {
                    q2++;
                }
            }

            if (position.x() > width / 2) {
                if (position.y() < height / 2) {
                    q3++;
                }
                if (position.y() > height / 2) {
                    q4++;
                }
            }
        }

        return q1 * q2 * q3 * q4;
    }

    private Vector2 predict(Vector2 position, Vector2 velocity, int width, int height, int seconds) {
        return new Vector2(
                Math.floorMod(position.x() + (velocity.x() * seconds), width),
                Math.floorMod(position.y() + (velocity.y() * seconds), height)
        );
    }

    record Robot(Vector2 position, Vector2 velocity) {
    }

    private List<Robot> parse(Input input) {
        return input.lines().stream()
                .map(line -> line.replaceAll("[pv=]", ""))
                .map(Utils::parseInts)
                .map(ints -> new Robot(new Vector2(ints.get(0), ints.get(1)), new Vector2(ints.get(2), ints.get(3))))
                .toList();
    }

}
