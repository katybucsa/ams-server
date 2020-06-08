package ro.ubbcluj.cs.ams.assignment.cache;

public interface CircuitBreakerService {

    <T> T getStableValue(StableValueParameter parameter);

    void evictValue(EvictValueParameter parameter);
}
