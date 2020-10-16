package tongji.timeautomateutil.util.comparator;

import tongji.timeautomateutil.timeautomate.TimeGuardElement;
import tongji.timeautomateutil.timeautomate.Transition;

import java.util.Comparator;
import java.util.List;

public class TranComparator implements Comparator<Transition> {

    @Override
    public int compare(Transition o1, Transition o2) {
        int var1 = o1.getSourceId() - o2.getSourceId();
        if(var1 != 0){
            return var1;
        }
        int var2 = o1.getSymbol().compareTo(o2.getSymbol());
        if(var2 != 0){
            return var2;
        }
        List<TimeGuardElement> o1TimeGuardElements = o1.getTimeGuard().getTimeGuardElements();
        List<TimeGuardElement> o2TimeGuardElements = o2.getTimeGuard().getTimeGuardElements();
        o1TimeGuardElements.sort(new TimeGuardElementComparator());
        o2TimeGuardElements.sort(new TimeGuardElementComparator());
        if(o1TimeGuardElements.size() == 0) {
            return -1;
        }
        if (o2TimeGuardElements.size() == 0) {
            return 1;
        }
        TimeGuardElement o1Element = o1TimeGuardElements.get(0);
        TimeGuardElement o2Element = o2TimeGuardElements.get(0);
        return o1Element.compareTo(o2Element);
    }
}
