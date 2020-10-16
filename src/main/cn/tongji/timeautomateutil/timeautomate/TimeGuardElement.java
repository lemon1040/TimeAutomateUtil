package tongji.timeautomateutil.timeautomate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tongji.timeautomateutil.timeword.LogicAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeGuardElement implements Cloneable, Comparable<TimeGuardElement> {

    public static final int MAX_TIME = 1000;

    private boolean lowerBoundOpen;

    private boolean upperBoundOpen;

    private int lowerBound;

    private int upperBound;

    private Clock clock;

    public boolean isLowerBoundClose() {
        return !lowerBoundOpen;
    }

    public boolean isUpperBoundClose() {
        return !upperBoundOpen;
    }

    public TimeGuardElement(String pattern, Clock clock) {
        this.clock = clock;
        pattern = pattern.trim();
        int size = pattern.length();
        char firstChar = pattern.charAt(0);
        char lastChar = pattern.charAt(size - 1);

        switch (firstChar) {
            case '[':
                setLowerBoundOpen(false);
                break;
            case '(':
                setLowerBoundOpen(true);
                break;
            default:
                throw new RuntimeException("guard pattern error");
        }

        switch (lastChar) {
            case ']':
                setUpperBoundOpen(false);
                break;
            case ')':
                setUpperBoundOpen(true);
                break;
            default:
                throw new RuntimeException("guard pattern error");
        }

        String[] numbers = pattern.split("\\,|\\[|\\(|\\]|\\)");
        if (numbers.length != 3) {
            throw new RuntimeException("guard pattern error");
        }

        int lowerBound, upperBound = 0;

        try {
            String num1 = numbers[1];
            String num2 = numbers[2];
            lowerBound = Integer.parseInt(num1);
            upperBound = num2.equals("+") ? MAX_TIME : Integer.parseInt(num2);
        } catch (Exception e) {
            throw new RuntimeException("guard pattern error");
        }

        setLowerBound(lowerBound);
        setUpperBound(upperBound);
    }

    /**
     *
     */
    public static TimeGuardElement bottomGuard(LogicAction action, Clock clock) {
        double time = action.getValue();
        boolean leftOpen, rightOpen;
        int left, right;
        if (time == (int) time) {
            leftOpen = false;
            left = (int) time;
            rightOpen = false;
            right = (int) time;
        } else {
            leftOpen = true;
            left = (int) time;
            rightOpen = true;
            right = (int) time + 1;
        }
        return new TimeGuardElement(leftOpen, rightOpen, left, right, clock);
    }


    //转成整型再比较
    public boolean isPass(double doubleValue) {
        int intValue = (int) ((doubleValue + 0.05) * 10);

        int lowerBound = getLowerBound() * 10;
        int upperBound = getUpperBound() * 10;

        if (isLowerBoundOpen() && isUpperBoundOpen()) {
            if (intValue > lowerBound && intValue < upperBound) {
                return true;
            }
        }
        if (isLowerBoundClose() && isUpperBoundOpen()) {
            if (intValue >= lowerBound && intValue < upperBound) {
                return true;
            }
        }
        if (isUpperBoundOpen() && isUpperBoundClose()) {
            if (intValue > lowerBound && intValue <= upperBound) {
                return true;
            }
        }
        if (isUpperBoundClose() && isLowerBoundClose()) {
            if (intValue >= lowerBound && intValue <= upperBound) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (isLowerBoundOpen()) {
            stringBuilder.append("(");
        } else {
            stringBuilder.append("[");
        }
        stringBuilder.append(lowerBound).append(",").append(upperBound);
        if (isUpperBoundOpen()) {
            stringBuilder.append(")");
        } else {
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }

    @Override
    public TimeGuardElement clone() throws CloneNotSupportedException {
        TimeGuardElement o =  (TimeGuardElement)super.clone();
        o.clock = clock.clone();
        return o;
    }

    @Override
    public int compareTo(TimeGuardElement o) {
        int var1 = getClock().getName().compareTo(o.getClock().getName());
        if(var1 != 0){
            return var1;
        }
        int var3 = getLowerBound() - o.getLowerBound();
        if(var3 !=0){
            return var3;
        }
        int var4 = getUpperBound() - o.getUpperBound();
        if(var4 != 0){
            return var4;
        }
        return -1;
    }
}