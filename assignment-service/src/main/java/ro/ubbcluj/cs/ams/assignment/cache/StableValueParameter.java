package ro.ubbcluj.cs.ams.assignment.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
@AllArgsConstructor
public class StableValueParameter<T> {

    private String cachePrefix;
    private long crpInSeconds;
    private String objectCacheKey;
    private Supplier<T> targetServiceAction;
    private DisasterStrategy disasterStrategy;

    public StableValueParameter(
            String cachePrefix,
            String objectCacheKey,
            long crpInSeconds,
            Supplier<T> targetServiceAction) {
        this.cachePrefix = cachePrefix;
        this.objectCacheKey = objectCacheKey;
        this.crpInSeconds = crpInSeconds;
        this.targetServiceAction = targetServiceAction;
      //  this.disasterStrategy = new ThrowExceptionDisasterStrategy();
    }
}
