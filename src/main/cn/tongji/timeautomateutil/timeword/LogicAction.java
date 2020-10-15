package tongji.timeautomateutil.timeword;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogicAction implements Action {
    private String Symbol;
    private double value;
    private boolean reset;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString(){
        String stringBuilder = "(" +
                getSymbol() +
                "," +
                getValue() +
                "," +
                (isReset() ? "r" : "n") +
                ")";
        return stringBuilder;
    }

}
