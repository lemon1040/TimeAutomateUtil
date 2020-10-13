package tongji.timeautomateutil.timeautomate;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor

public class TimeGuard {
    private List<TimeGuardElement> timeGuardElements;

    public TimeGuard() {
        timeGuardElements = new ArrayList<>();
    }

    public void add(TimeGuardElement timeGuardElement){
        timeGuardElements.add(timeGuardElement);
    }
}
