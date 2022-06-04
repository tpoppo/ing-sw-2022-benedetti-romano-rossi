package it.polimi.ingsw.view;

import java.net.URL;
import java.util.HashMap;

public class ResourcesCache {

    private static final HashMap<String, URL> resourceCache = new HashMap<>();
    synchronized static URL getResource(String s){ //TODO: We must do more profiling. I am not sure if it is better
        // return ResourcesCache.class.getResource(s);
        if(!resourceCache.containsKey(s)) {
            resourceCache.put(s, ResourcesCache.class.getResource(s));
        }
        return resourceCache.get(s);

    }

}
