package com.onefeedsdk.app;

import java.util.HashMap;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 17-July-2018
 * Time: 13:02
 */
public class RuntimeStore {

    private static RuntimeStore runtimeStore;

    //Key-String, Value can be any object
    private final HashMap<String, Object> runtimeMap;

    private RuntimeStore() {
        runtimeMap = new HashMap<>();
    }

    public static void init() {
        runtimeStore = new RuntimeStore();
    }

    public static RuntimeStore getInstance() {

        return runtimeStore;
    }

    public Object getValueFor(String key) {
        return runtimeMap.get(key);
    }

    public void putKeyValues(String key, Object value) {
        runtimeMap.put(key, value);
    }
}
