package tongji.timeautomateutil.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import equivalence.ta.Clock;
import equivalence.ta.TA;
import equivalence.ta.TaTimeGuard;
import equivalence.ta.TaTransition;
import ota.*;
import tongji.timeautomateutil.timeautomate.*;
import tongji.timeautomateutil.util.comparator.TranComparator;
import util.comparator.LocationComparator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TimeAutomateUtil {

    public static TimeAutomate getOTAFromJsonFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String str = null;
        StringBuilder json = new StringBuilder();
        while ((str = reader.readLine()) != null) {
            json.append(str);
        }
        TimeAutomate ota = getOTAFromJson(json.toString());
        ota.getTransitionList().sort(new TranComparator());
        return ota;
    }

    public static TimeAutomate getOTAFromJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String name = jsonObject.getString("name");

        JSONArray sigmaArray = jsonObject.getJSONArray("sigma");
        Set<String> sigma = new HashSet<>(sigmaArray.size());
        for (Object o : sigmaArray) {
            sigma.add((String) o);
        }

        JSONArray clockArray = jsonObject.getJSONArray("clock");
        Map<String, Clock> clockMap = new HashMap<>(clockArray.size());
        for (Object o : clockArray) {
            clockMap.put((String) o, new Clock((String) o));
        }

        List<Location> locationList = new ArrayList<>();
        JSONArray locationArray = jsonObject.getJSONArray("l");
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < locationArray.size(); i++) {
            list.add(locationArray.getIntValue(i));
        }
        JSONArray acceptArray = jsonObject.getJSONArray("accept");
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < acceptArray.size(); i++) {
            set.add(acceptArray.getIntValue(i));
        }
        int initId = jsonObject.getInteger("init");
        for (int id : list) {
            Location location = new Location(id);
            location.setName("" + id);
            location.setAccept(set.contains(id));
            location.setInit(id == initId);
            locationList.add(location);
        }

        Map<Integer, Location> locationMap = new HashMap<>();
        for (Location l : locationList) {
            locationMap.put(l.getId(), l);
        }

        List<Transition> transitionList = convertTranToList(locationMap, clockMap, jsonObject.getJSONObject("tran"));

        return new TimeAutomate(name, clockMap, sigma, locationList, transitionList);
    }

    /**
     * 把JSON对象转化为Transition 的list
     */
    private static List<Transition> convertTranToList(Map<Integer, Location> locationMap, Map<String, Clock> clockMap, JSONObject tranJsonObject) {
        int size = tranJsonObject.size();
        List<Transition> transitionList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            JSONArray array = tranJsonObject.getJSONArray(String.valueOf(i));
            // source of transition
            int sourceId = array.getInteger(0);
            Location sourceLocation = locationMap.get(sourceId);

            // input action
            String action = array.getString(1);

            // timeGuard of transition
            JSONObject timeGuardJSON = array.getJSONObject(2);
            TimeGuard timeGuard = new TimeGuard();
            for (Map.Entry<String, Object> entry : timeGuardJSON.entrySet()) {
                TimeGuardElement element = new TimeGuardElement((String) entry.getValue(), clockMap.get(entry.getKey()));
                timeGuard.add(element);
            }

            // transition reset or not
            JSONObject resetJSON = array.getJSONObject(3);
            Set<Clock> resetClockSet = new HashSet<>();
            for (Map.Entry<String, Object> entry : resetJSON.entrySet()) {
                Clock clock = clockMap.get(entry.getKey());
                if (entry.getValue().equals("r")) {
                    resetClockSet.add(clock);
                }
            }

            // target of transition
            int targetId = array.getInteger(4);
            Location targetLocation = locationMap.get(targetId);
            Transition transition = new Transition(sourceLocation, targetLocation, timeGuard, action, resetClockSet);
            transitionList.add(transition);
        }

        return transitionList;
    }

    public static void completeOTA(OTA ota) {

        List<Transition> transitionList = ota.getTransitionList();
        List<Transition> complementaryTranList = new ArrayList<>();
        List<Location> locationList = ota.getLocationList();
        Set<String> sigma = ota.getSigma();

        Location sink = new Location(ota.size() + 1, "sink", false, false);
        for (Location location : locationList) {
            for (String symbol : sigma) {
                List<Transition> transitions = ota.getTransitions(location, symbol, null);
                if (transitions.isEmpty()) {
                    Transition transition = Transition.builder()
                            .sourceLocation(location)
                            .targetLocation(sink)
                            .symbol(symbol)
                            .reset("r")
                            .timeGuard(new TimeGuard("[0,+)"))
                            .build();
                    complementaryTranList.add(transition);
                    continue;
                }
                complementaryTranList.addAll(TransitionUtil.complementary(transitions, sink));
            }
        }

        if (complementaryTranList.isEmpty()) {
            return;
        }

        transitionList.addAll(complementaryTranList);
        locationList.add(sink);

    }

    public static OTA removeSink(OTA ota) {
        List<Transition> newTransitionList = new ArrayList<>(ota.getTransitionList());
        List<Location> locationList = ota.getLocationList();
        Set<String> sigma = ota.getSigma();

        List<Location> newLocationList = new ArrayList<>();

        for (int i = 0; i < locationList.size(); i++) {
            Location location = locationList.get(i);
            if (location.isAccept()) {
                newLocationList.add(location);
            } else {
                List<Transition> list1 = ota.getTransitions(location, null, null);
                List<Transition> list2 = ota.getTransitions(null, null, location);
                newTransitionList.removeAll(list1);
                newTransitionList.removeAll(list2);
            }
        }


        return new OTA(ota.getName(), sigma, newLocationList, newTransitionList);
    }

    public static OTA evidToOTA(OTA evidenceOTA) {
        for (Location l : evidenceOTA.getLocationList()) {
            for (String action : evidenceOTA.getSigma()) {
                List<Transition> transitionList1 = evidenceOTA.getTransitions(l, action, null);
                transitionList1.sort(new TranComparator());
                for (int i = 0; i < transitionList1.size(); i++) {
                    if (i < transitionList1.size() - 1) {
                        TimeGuard timeGuard1 = transitionList1.get(i).getTimeGuard();
                        TimeGuard timeGuard2 = transitionList1.get(i + 1).getTimeGuard();
                        timeGuard1.setUpperBound(timeGuard2.getLowerBound());
                        timeGuard1.setUpperBoundOpen(!timeGuard2.isLowerBoundOpen());
                    } else {
                        TimeGuard timeGuard1 = transitionList1.get(i).getTimeGuard();
                        timeGuard1.setUpperBound(TimeGuard.MAX_TIME);
                        timeGuard1.setUpperBoundOpen(false);
                    }
                }
            }
        }
        return evidenceOTA;
    }

    public static TA getCartesian(OTA ota1, OTA ota2) {
        //初始化时钟
        Clock c1 = new Clock("c1");
        Clock c2 = new Clock("c2");
        Set<Clock> clockSet = new HashSet<>();
        clockSet.add(c1);
        clockSet.add(c2);

        //初始化sigma字符集
        Set<String> sigma = new HashSet<>();
        sigma.addAll(ota1.getSigma());
        sigma.addAll(ota2.getSigma());

        if (preCheck(ota1.getLocationList()) == false || preCheck(ota2.getLocationList()) == false) {
            throw new RuntimeException("locationList 出错");
        }


        Map<Integer, Location> map = new HashMap<>();
        for (Location location1 : ota1.getLocationList()) {
            for (Location location2 : ota2.getLocationList()) {
                String name = location1.getName() + "@" + location2.getName();
                int id = (location2.getId() - 1) * ota1.size() + location1.getId();
                boolean init = location1.isInit() && location2.isInit();
                boolean accpted = location1.isAccept() && location2.isAccept();
                Location location = new Location(id, name, init, accpted);
                map.put(id, location);
            }
        }
        List<Location> locationList = new ArrayList<>(map.values());


        List<TaTransition> taTransitionList = new ArrayList<>();
        for (Transition t1 : ota1.getTransitionList()) {
            for (Transition t2 : ota2.getTransitionList()) {
                if (!t1.getSymbol().equals(t2.getSymbol())) {
                    continue;
                }
                TaTimeGuard taTimeGuard = new TaTimeGuard();
                taTimeGuard.add(t1.getTimeGuard(), c1);
                taTimeGuard.add(t2.getTimeGuard(), c2);

                Set<Clock> resetClockSet = new HashSet<>();
                if (t1.isReset()) {
                    resetClockSet.add(c1);
                }
                if (t2.isReset()) {
                    resetClockSet.add(c2);
                }

                int sourceId = (t2.getSourceId() - 1) * ota1.size() + t1.getSourceId();
                int targetId = (t2.getTargetId() - 1) * ota1.size() + t1.getTargetId();
                Location source = map.get(sourceId);
                Location target = map.get(targetId);
                TaTransition taTransition = new TaTransition(source, target, t1.getSymbol(), taTimeGuard, resetClockSet);
                taTransitionList.add(taTransition);
            }
        }

        String name = ota1.getName() + ":" + ota2.getName();
        return new TA(name, clockSet, sigma, locationList, taTransitionList);
    }

    public static OTA getNegtiveOTA(OTA ota) {
        OTA neg = ota.copy();
        for (Location l : neg.getLocationList()) {
            l.setAccept(!l.isAccept());
        }
        return neg;
    }

    private static boolean preCheck(List<Location> locations) {
        locations.sort(new LocationComparator());
        for (int i = 1; i < locations.size(); i++) {
            if (locations.get(i).getId() - 1 != locations.get(i - 1).getId()) {
                return false;
            }
        }
        return true;
    }

}

