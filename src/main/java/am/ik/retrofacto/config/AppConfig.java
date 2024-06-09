package am.ik.retrofacto.config;

import java.time.Clock;
import java.util.Map;
import java.util.function.Predicate;

import io.micrometer.core.instrument.config.MeterFilter;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

	private final Predicate<String> uriFilter = uri -> {
		boolean deny = uri != null && (uri.equals("/readyz") || uri.equals("/livez") || uri.startsWith("/actuator")
				|| uri.startsWith("/_static"));
		return !deny;
	};

	@Bean
	public MeterRegistryCustomizer<?> meterRegistryCustomizer() {
		final Predicate<String> negate = uriFilter.negate();
		return registry -> registry.config() //
			.meterFilter(MeterFilter.deny(id -> {
				final String uri = id.getTag("uri");
				return negate.test(uri);
			}));
	}

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	@Bean
	@SuppressWarnings("deprecation")
	public PasswordEncoder passwordEncoder() {
		String idForEncode = "bcrypt";
		return new DelegatingPasswordEncoder(idForEncode,
				Map.of(idForEncode, new BCryptPasswordEncoder(), "noop", NoOpPasswordEncoder.getInstance()));
	}

}