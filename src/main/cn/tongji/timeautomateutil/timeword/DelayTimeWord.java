package tongji.timeautomateutil.timeword;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class DelayTimeWord implements Cloneable {
    private List<DelayAction> actionList;

    public int size() {
        return actionList.size();
    }

    public DelayAction get(int i) {
        return actionList.get(i);
    }

    public static DelayTimeWord emptyWord() {
        return new DelayTimeWord(new ArrayList<>());
    }

    public boolean isEmpty() {
        return actionList.isEmpty();
    }

    public DelayTimeWord subWord(int fromIndex, int toIndex) {
        try {
            List<DelayAction> subList = getActionList().subList(fromIndex, toIndex);
            return new DelayTimeWord(subList);
        } catch (Exception e) {
            return emptyWord();
        }
    }

    @Override
    public DelayTimeWord clone() throws CloneNotSupportedException {
        DelayTimeWord timeWord = (DelayTimeWord) super.clone();
        timeWord.actionList = new ArrayList<>();
        for (DelayAction action : actionList) {
            timeWord.actionList.add(action.clone());
        }
        return timeWord;
    }
}
