package cz.metacentrum.registrar.rest.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CachingConfig {

	/**
	 * CacheManager for caching access tokens introspections to reduce calls to introspection endpoint.
	 * Each token is cached for 30 seconds after making token introspection request.
	 * Max size is 5000.
	 * We can also used EHCache instead of Guava Cache builder. Default Spring ConcurrentMapCacheManager
	 * doesn't allow time expiration for entries.
	 */
	@Bean
	public CacheManager cacheManager() {
//		return new ConcurrentMapCacheManager("token_introspection");
		ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {

			@Override
			protected Cache createConcurrentMapCache(final String name) {
				return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS)
						.maximumSize(5000).build().asMap(), false);
			}
		};

		cacheManager.setCacheNames(Collections.singletonList("token_introspection"));
		return cacheManager;
	}
}
