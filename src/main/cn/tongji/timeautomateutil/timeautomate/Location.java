package tongji.timeautomateutil.timeautomate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tongji.timeautomateutil.timeword.LogicTimeWord;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location implements Cloneable {
    private int id;
    private String name;
    private boolean init;
    private boolean accept;
    private LogicTimeWord logicTimeWord;

    public Location(int id, String name, boolean init, boolean accept) {
        this.id = id;
        this.name = name;
        this.init = init;
        this.accept = accept;
    }

    public Location(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Location(int id) {
        this.id = id;
    }

    public Location clone() throws CloneNotSupportedException {
        Location o = (Location) super.clone();
        o.logicTimeWord = logicTimeWord.clone();
        return o;
    }

}
