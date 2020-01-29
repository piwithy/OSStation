package fr.piwithy;

import java.util.HashMap;
import java.util.Map;

public class RailwayStation {

    public final HashMap<Integer, Boolean> platforms;
    public final String name;

    public RailwayStation(String name, int n_platforms) {
        this.name = name;
        this.platforms = new HashMap<>();
        for (int i = 1; i <= n_platforms; i++)
            this.platforms.put(i, true);
    }

    public synchronized int getFreePlatform() {
        for (Map.Entry<Integer, Boolean> entry : this.platforms.entrySet()) {
            if (entry.getValue()) {
                entry.setValue(Boolean.FALSE);
                return entry.getKey();
            }
        }
        return -1;
    }

}
