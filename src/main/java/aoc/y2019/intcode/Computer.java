package aoc.y2019.intcode;

public class Computer {

    private final Memory memory;

    public Computer(Memory memory) {
        this.memory = memory;
    }

    public Memory memory() {
        return memory;
    }

    public void run() {
        var pc = 0;
        var exit = false;

        while (!exit) {
            var opcode = memory.read(pc++);

            switch (opcode) {
                case 1 -> {  // add
                    final var address1 = memory.read(pc++);
                    final var address2 = memory.read(pc++);
                    final var resultAddress = memory.read(pc++);

                    var temp = memory.read(address1) + memory.read(address2);
                    memory.write(resultAddress, temp);
                }
                case 2 -> { // multiply
                    final var address1 = memory.read(pc++);
                    final var address2 = memory.read(pc++);
                    final var resultAddress = memory.read(pc++);

                    var temp = memory.read(address1) * memory.read(address2);
                    memory.write(resultAddress, temp);
                }
                case 99 -> exit = true;
                default -> throw new IllegalStateException("Unknown opcode: " + opcode);
            }
        }
    }
}
