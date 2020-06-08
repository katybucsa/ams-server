package ro.ubbcluj.cs.ams.utils.health;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServicesThatFetchedTheRegistry {

    public static AtomicInteger number=new AtomicInteger(0);
    public static Set<String> services = Collections.newSetFromMap(new ConcurrentHashMap<>());
}
