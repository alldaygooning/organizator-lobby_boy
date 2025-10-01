package pencil_utensil.lobby_boy.network.security.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

	@Bean
	FilterRegistrationBean<ApiFilter> jwtFilterRegistration(ApiFilter jwtFilter) {
		FilterRegistrationBean<ApiFilter> reg = new FilterRegistrationBean<>(jwtFilter);
		reg.setEnabled(false);
		return reg;
	}
}
