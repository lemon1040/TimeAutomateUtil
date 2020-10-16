package tongji.timeautomateutil.timeword;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DelayAction implements Cloneable{
    private String Symbol;
    private double value;

    @Override
    public String toString(){
        return "(" +
                getSymbol() +
                "," +
                getValue() +
                ")";
    }

    @Override
    public DelayAction clone() throws CloneNotSupportedException {
        return (DelayAction)super.clone();
    }
}
