package tongji.timeautomateutil.timeautomate;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class TimeGuard implements Cloneable {
    private List<TimeGuardElement> timeGuardElements;

    public TimeGuard() {
        timeGuardElements = new ArrayList<>();
    }

    public void add(TimeGuardElement timeGuardElement) {
        timeGuardElements.add(timeGuardElement);
    }

    @Override
    public TimeGuard clone() throws CloneNotSupportedException {
        TimeGuard guard = (TimeGuard)super.clone();
        guard.timeGuardElements = new ArrayList<>(timeGuardElements.size());
        for(TimeGuardElement t : timeGuardElements) {
            guard.timeGuardElements.add(t.clone());
        }
        return guard;
    }
}
