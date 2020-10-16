package tongji.timeautomateutil.timeautomate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transition implements Cloneable {
    private Location sourceLocation;
    private Location targetLocation;
    private TimeGuard timeGuard;
    private String symbol;
    private Set<Clock> resetClockSet;

    public boolean isReset(Clock clock) {
        return resetClockSet.contains(clock);
    }

    public int getSourceId() {
        return sourceLocation.getId();
    }

    public int getTargetId() {
        return targetLocation.getId();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(sourceLocation.getId()).append(", ").append(symbol).append(",");
        for (TimeGuardElement t : timeGuard.getTimeGuardElements()) {
            sb.append(t)
                    .append("-")
                    .append(t.getClock().getName())
                    .append(",")
                    .append(isReset(t.getClock()) ? "r" : "n")
                    .append(" & ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(", ").append(targetLocation.getId()).append("]");
        return sb.toString();
    }
}
