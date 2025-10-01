package pencil_utensil.lobby_boy.network.security.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiFilter extends OncePerRequestFilter {

	private static final String API_KEY_HEADER = "X-API-KEY";
	private final String key;

	public ApiFilter(@Value("${api.key}") String key) {
		this.key = key;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String apiKey = request.getHeader(API_KEY_HEADER);
		if (apiKey == null || !apiKey.equals(key)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Invalid API Key");
			System.out.println(apiKey + " " + key);
			return;
		}

		UsernamePasswordAuthenticationToken auth =
				new UsernamePasswordAuthenticationToken("microservice", null,
						List.of(new SimpleGrantedAuthority("ROLE_MICROSERVICE")));
		SecurityContextHolder.getContext().setAuthentication(auth);

		filterChain.doFilter(request, response);
	}
}
