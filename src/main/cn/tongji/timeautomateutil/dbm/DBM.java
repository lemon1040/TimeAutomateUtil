package tongji.timeautomateutil.dbm;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tongji.timeautomateutil.timeautomate.Clock;
import tongji.timeautomateutil.timeautomate.TimeGuard;
import tongji.timeautomateutil.timeautomate.TimeGuardElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 表示一个zone 由一组时钟值和时钟约束矩阵组成
 */
public class DBM implements Cloneable{
    @Getter
    private List<Clock> clockList;

    @Getter
    @Setter
    private Value[][] matrix;

    public DBM(List<Clock> clockList, Value[][] matrix) {
        if (matrix.length != clockList.size() + 1) {
            throw new RuntimeException("Illegal input for building DBM!!!!!\n " +
                    "matrix.length should equal to clockList.size() + 1");
        }
        this.clockList = clockList;
        this.matrix = matrix;
    }

    public static DBM getInitDBM(List<Clock> clockList) {
        //因为有零时钟，所以矩阵大小为时钟数加一
        int n = clockList.size() + 1;
        Value[][] matrix = new Value[n][n];

        //初始化为>=0
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = new Value(0, true);
            }
        }

        //设置初始范围 <正无穷
        for (int i = 1; i < n; i++) {
            matrix[i][0] = new Value(Integer.MAX_VALUE, false);
        }
        return new DBM(clockList, matrix);
    }

    /**
     * DBM的大小
     * 包含时钟值的个数 + 1 (零时钟值)
     */
    public int size() {
        return clockList.size() + 1;
    }

    /**
     * 使用Floyd算法求DBM最短边
     */
    public void canonical() {
        for (int k = 0; k < size(); k++) {
            for (int i = 0; i < size(); i++) {
                for (int j = 0; j < size(); j++) {
                    Value v = Value.add(matrix[i][k], matrix[k][j]);
                    if (matrix[i][j].compareTo(v) > 0) {
                        matrix[i][j] = v;
                    }
                }
            }
        }
    }

    /**
     * 判断DBM的一致性
     * 假如有：
     * clock[i] - clock[j] <= or < D[i+1][j+1]
     * clock[j] - clock[i] <= or < D[j+1][i+1]
     * 即：
     * -D[j+1][i+1] <= or < clock[i] - clock[j] <= or < D[i+1][j+1]
     * <p>
     * 如果DBM是不一致的则存在一个i使得：
     * -D[j+1][i+1] > D[i+1][j+1]  ===>  D[i+1][j+1] + D[j+1][i+1] < 0
     * ===> D[i+1][i+1] < 0 <strong>（DBM is canonical）</strong>
     * <p>
     * 如果不存在这样的i则DBM是一致的
     */
    public Boolean isConsistent() {
        Value zeroValue = new Value(0, true);
        for (int i = 0; i < size(); i++) {
            if (matrix[i][i].compareTo(zeroValue) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 包含关系判断
     * 判断传入的dbm是否被当前dbm包含
     */
    public boolean include(DBM dbm) {
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (matrix[i][j].compareTo(dbm.matrix[i][j]) < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断dbm矩阵的第i项和第j项时钟的新约束是否可以应用到当前dbm上
     */
    public boolean satisfied(int i, int j, Value value) throws CloneNotSupportedException {
        // 复制dbm数组，并将value应用到复制好的dbm数组
        DBM cache = clone();
        Value[][] copyMatrix = cache.getMatrix();
        copyMatrix[i][j] = Value.add(copyMatrix[i][j], value);
        // 判断形成的新dbm数组是否满足一致性
        return cache.isConsistent();
    }


    //up操作,把上限变为正无穷
    public void up() {
        for (int i = 1; i < size(); i++) {
            matrix[i][0] = new Value(Integer.MAX_VALUE, false);
        }
    }

    //and操作
    public void and(TimeGuardElement timeGuardElement) {

        int index = clockList.indexOf(timeGuardElement.getClock());
        Value upperBound = new Value(timeGuardElement.getUpperBound(), !timeGuardElement.isUpperBoundOpen());
        if (upperBound.compareTo(matrix[index + 1][0]) < 0) {
            matrix[index + 1][0] = upperBound;
        }
        Value lowerBound = new Value(timeGuardElement.getLowerBound() * (-1), !timeGuardElement.isLowerBoundOpen());
        if (lowerBound.compareTo(matrix[0][index + 1]) < 0) {
            matrix[0][index + 1] = lowerBound;
        }
    }

    //and操作，TaTimeGuard
    public void and(TimeGuard timeGuards) {
        for (TimeGuardElement guard : timeGuards.getTimeGuardElements()) {
            and(guard);
        }
    }

    //reset操作
    public void reset(Clock c) {
        int index = clockList.indexOf(c) + 1;
        for (int i = 0; i < size(); i++) {
            matrix[index][i] = matrix[0][i];
            matrix[i][index] = matrix[i][0];
        }
    }

    //reset操作，reset一个集合
    public void reset(Set<Clock> clockSet) {
        for (Clock c : clockSet) {
            reset(c);
        }
    }

    public DBM clone() throws CloneNotSupportedException {
        DBM o = (DBM)super.clone();
        o.clockList = new ArrayList<>(clockList.size());
        o.matrix =  new Value[size()][size()];
        for(Clock clock : clockList) {
            o.clockList.add(clock.clone());
        }
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                o.matrix[i][j] = matrix[i][j].clone();
            }
        }
        return o;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("the dbm matrix is:\n");
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (matrix[i][j].getValue() == Integer.MAX_VALUE) {
                    sb.append("∞").append("<").append(" \t");
                } else {
                    sb.append(matrix[i][j].getValue());
                    if (matrix[i][j].isEqual()) {
                        sb.append(" <=");
                    } else {
                        sb.append(" <");
                    }
                    sb.append(" \t");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
