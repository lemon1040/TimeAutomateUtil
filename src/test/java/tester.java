import tongji.timeautomateutil.dbm.DBM;
import tongji.timeautomateutil.dbm.Value;
import tongji.timeautomateutil.timeautomate.Clock;

import java.util.ArrayList;
import java.util.List;

public class tester {
    public static void main(String[] args) {
        List<Clock> clockList = new ArrayList<>();
        Value[][] matrix = new Value[2][2];
        clockList.add(new Clock("hello"));
        for (int i = 0; i < clockList.size() + 1; i++) {
            for (int j = 0; j < clockList.size() + 1; j++) {
                matrix[i][j] = new Value(0, true);
            }
        }
        DBM dbm = new DBM(clockList, matrix);
        DBM b = null;
        try {
            b = dbm.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if(b!= null) {
            System.out.println("done"+ b.toString());
        }

    }
}
