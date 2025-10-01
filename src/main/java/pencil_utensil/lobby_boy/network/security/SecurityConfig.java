package pencil_utensil.lobby_boy.network.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import pencil_utensil.lobby_boy.network.security.filter.ApiFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final ApiFilter apiFilter;

	SecurityConfig(ApiFilter apiFilter) {
		this.apiFilter = apiFilter;
	}

	@Bean
	@Order(1)
	SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
		return commonConfig(http)
				.securityMatcher("/api/users/**")
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain secureChain(HttpSecurity http) throws Exception {
		return commonConfig(http)
				.securityMatcher("/api/jwt/**")
				.authorizeHttpRequests(auth -> auth.anyRequest().hasRole("MICROSERVICE"))
				.addFilterBefore(apiFilter, AuthorizationFilter.class)
				.build();
	}

	private HttpSecurity commonConfig(HttpSecurity http) throws Exception {
		return http
				.csrf((csrf) -> csrf.disable()) // Do not need it because this attacks has to do with session cookies
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	}
}
