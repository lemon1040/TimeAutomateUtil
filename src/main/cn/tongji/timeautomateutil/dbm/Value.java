package tongji.timeautomateutil.dbm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Value implements Comparable<Value>, Cloneable {
    private int value;
    private boolean equal;

    public static Value add(Value v2, Value v3) {
        int value = v2.getValue() + v3.getValue();
        boolean equal = v2.isEqual() && v3.isEqual();
        return new Value(value, equal);
    }

    public int compareTo(Value o) {
        if (this.getValue() < o.getValue()) {
            return -1;
        }
        if (this.getValue() == o.getValue()) {
            // < < ≤
            if (!this.equal && o.equal) {
                return -1;
            }

            if (this.equal == o.equal) {
                return 0;
            }
        }
        return 1;
    }

    @Override
    public Value clone() throws CloneNotSupportedException {
        return (Value)super.clone();
    }
}
