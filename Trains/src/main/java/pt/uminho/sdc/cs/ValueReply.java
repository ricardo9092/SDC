package pt.uminho.sdc.cs;

public class ValueReply<T> extends Reply {
    private final T value;

    public ValueReply(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public String toString() {
        return "Value: "+value;
    }
}
