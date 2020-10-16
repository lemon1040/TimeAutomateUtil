package tongji.timeautomateutil.timeautomate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeAutomate {
    private String name;
    private Map<String, Clock> clockSet;
    private Set<String> sigma;
    private List<Location> locationList;
    private List<Transition> transitionList;


    public Location getInitLocation() {
        for (Location location : locationList) {
            if (location.isInit()) {
                return location;
            }
        }
        return null;
    }

    public List<Location> getAcceptedLocations() {
        List<Location> list = new ArrayList<>();
        for (Location location : locationList) {
            if (location.isAccept()) {
                list.add(location);
            }
        }
        return list;
    }

    public List<Transition> getTransitions(Location fromLocation,
                                           String symbol,
                                           Location toLocation) {
        List<Transition> list = new ArrayList<>(transitionList);
        Iterator<Transition> iterator = list.iterator();
        while (iterator.hasNext()) {
            Transition t = iterator.next();
            int tSourceId = t.getSourceId();
            int tTargetId = t.getTargetId();
            String tSymbol = t.getSymbol();

            if (fromLocation != null) {
                int fromId = fromLocation.getId();
                if (tSourceId != fromId) {
                    iterator.remove();
                    continue;
                }
            }

            if (symbol != null) {
                if (!StringUtils.equals(tSymbol, symbol)) {
                    iterator.remove();
                    continue;
                }
            }

            if (toLocation != null) {
                int toId = toLocation.getId();
                if (toId != tTargetId) {
                    iterator.remove();
                    continue;
                }
            }
        }
        return list;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\t").append("\"sigma\":[");
        for (String action : getSigma()) {
            sb.append("\"" + action + "\",");
        }
        sb.deleteCharAt(sb.length() - 1).append("],\n\t").append("\"init\":");
        int init = getInitLocation().getId();
        sb.append(init).append(",\n\t").append("\"name\":\"").append(getName()).append("\"\n\t");
        sb.append("\"s\":[");
        for (Location l : getLocationList()) {
            sb.append(l.getId()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1).append("]\n\t\"tran\":{\n");

        for (int i = 0; i < getTransitionList().size(); i++) {
            Transition t = getTransitionList().get(i);
            sb.append("\t\t\"").append(i).append(t.toString()).append(",\n");
        }
        sb.deleteCharAt(sb.length() - 2);
        sb.append("\t},\n\t").append("\"accpted\":[");
        for (Location l : getAcceptedLocations()) {
            sb.append(l.getId()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1).append("]\n}");
        return sb.toString();
    }
}
