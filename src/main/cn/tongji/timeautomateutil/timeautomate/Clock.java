package tongji.timeautomateutil.timeautomate;

import java.util.Objects;

public class Clock implements Cloneable {
    private final String name;
    private double value;

    public Clock(String name){
        this.name = name;
    }

    public void delay(double delay){
        value+=delay;
    }

    public double getValue(){
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public Clock clone() throws CloneNotSupportedException {
        return (Clock)super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Clock)) {
            return false;
        }
        Clock clock = (Clock) o;
        return Objects.equals(getName(), clock.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
