package utils;

public class LongHolder {

    public static LongHolder longHolder(long l) {
        return new LongHolder(l);
    }

    private long value;

    public LongHolder(long value) {
        this.value = value;
    }

    public long get() {
        return value;
    }

    public void set(long value) {
        this.value = value;
    }
}
