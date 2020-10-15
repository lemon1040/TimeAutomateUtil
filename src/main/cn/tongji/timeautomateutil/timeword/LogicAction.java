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
    public LogicAction clone() throws CloneNotSupportedException {
        return (LogicAction)super.clone();
    }

    @Override
    public String toString(){
        return "(" +
                getSymbol() +
                "," +
                getValue() +
                "," +
                (isReset() ? "r" : "n") +
                ")";
    }

}
