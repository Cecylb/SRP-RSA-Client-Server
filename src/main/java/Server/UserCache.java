package Server;

import java.util.HashMap;
import java.util.Map;

public class UserCache {
    private static UserCache instance;
    private final Map<String, Account> cache = new HashMap<String, Account>();

    private UserCache() {
    }

    public static UserCache getInstance() {
        if(instance == null) {
            instance = new UserCache();
        }
        return instance;
    }

    public Map<String, Account> getCache() {
        return this.cache;
    }
}
