package sk.teamsoft.amf.util;

/**
 * Helper class similar to Pair from android support library
 * But with lazy initialization
 *
 * @author Dusan Bartos
 */
public final class Tuple2<F, S> {
    private F first;
    private S second;

    public Tuple2() {
    }

    public Tuple2(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public F getFirst() {
        return first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public S getSecond() {
        return second;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

        if (first != null ? !first.equals(tuple2.first) : tuple2.first != null) return false;
        return second != null ? second.equals(tuple2.second) : tuple2.second == null;

    }

    @Override public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
