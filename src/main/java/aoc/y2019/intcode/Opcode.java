package aoc.y2019.intcode;

public class Opcode {

    public static Opcode decode(int value) {
        return new Opcode(value);
    }

    private final int opcode;
    private int parameterModes;

    private Opcode(int value) {
        this.opcode = value % 100;
        this.parameterModes = value / 100;
    }

    public int opcode() {
        return opcode;
    }

    public int nextParameterMode() {
        var next = parameterModes % 10;
        parameterModes /= 10;
        return next;
    }
}
