package tongji.timeautomateutil.timeword;

public interface TimeWord {

    Action get(int i);

    boolean isEmpty();

    TimeWord subWord(int fromIndex, int toIndex);

}
