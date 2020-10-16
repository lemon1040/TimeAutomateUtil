package tongji.timeautomateutil.util.comparator;

import tongji.timeautomateutil.timeautomate.TimeGuardElement;
import tongji.timeautomateutil.timeautomate.Transition;

import java.util.Comparator;

public class TimeGuardElementComparator implements Comparator<TimeGuardElement> {
    @Override
    public int compare(TimeGuardElement o1, TimeGuardElement o2) {
        int var1 = o1.getClock().getName().compareTo(o2.getClock().getName());
        if(var1 != 0){
            return var1;
        }
        int var3 = o1.getLowerBound() - o2.getLowerBound();
        if(var3 !=0){
            return var3;
        }
        int var4 = o1.getUpperBound() - o2.getUpperBound();
        if(var4 != 0){
            return var4;
        }
        return -1;
    }
}
