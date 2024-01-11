package aoc.y2019.intcode;

public class Opcode {

    public static Opcode decode(long value) {
        return new Opcode(value);
    }

    private final int opcode;
    private long parameterModes;

    private Opcode(long value) {
        this.opcode = (int) (value % 100);
        this.parameterModes = value / 100;
    }

    public int opcode() {
        return opcode;
    }

    public int nextParameterMode() {
        var next = (int) (parameterModes % 10);
        parameterModes /= 10;
        return next;
    }
}
