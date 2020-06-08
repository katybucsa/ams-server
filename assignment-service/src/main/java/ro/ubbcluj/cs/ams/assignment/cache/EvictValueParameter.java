package ro.ubbcluj.cs.ams.assignment.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EvictValueParameter {

    private String cachePrefix;
    private String objectCacheKey;
}
