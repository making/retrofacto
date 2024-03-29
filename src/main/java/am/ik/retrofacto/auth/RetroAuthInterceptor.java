package am.ik.retrofacto.auth;

import java.util.Arrays;
import java.util.Optional;

import am.ik.retrofacto.retro.Board;
import am.ik.retrofacto.retro.RetroConstants;
import am.ik.retrofacto.retro.RetroService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RetroAuthInterceptor implements HandlerInterceptor {

	private final RetroService retroService;

	private final JwtDecoder jwtDecoder;

	public RetroAuthInterceptor(RetroService retroService, JwtDecoder jwtDecoder) {
		this.retroService = retroService;
		this.jwtDecoder = jwtDecoder;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String[] paths = request.getServletPath().split("/", 3);
		request.setAttribute(RetroConstants.RETRO_AUTHENTICATED, false);
		if (paths.length < 3 || !"boards".equals(paths[1])) {
			return true;
		}
		String slug = paths[2];
		Optional<Board> optionalBoard = this.retroService.findBySlug(slug);
		if (optionalBoard.isEmpty()) {
			return true;
		}
		Board board = optionalBoard.get();
		if (!StringUtils.hasText(board.getPassphrase())) {
			request.setAttribute(RetroConstants.RETRO_AUTHENTICATED, true);
			return true;
		}
		if (request.getCookies() != null) {
			Optional<Cookie> authCookie = Arrays.stream(request.getCookies())
				.filter(cookie -> (RetroConstants.RETRO_COOKIE_PREFIX + slug).equals(cookie.getName()))
				.findAny();
			if (authCookie.isPresent()) {
				String encoded = authCookie.get().getValue();
				try {
					Jwt jwt = this.jwtDecoder.decode(encoded);
					if (jwt.getAudience().contains(slug)) {
						request.setAttribute(RetroConstants.RETRO_AUTHENTICATED, true);
						return true;
					}
					else {
						throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid slug.");
					}
				}
				catch (JwtException e) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT.", e);
				}
			}
		}
		if (paths.length == 3 && "GET".equals(request.getMethod())) {
			return true;
		}
		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authentication required.");
	}

}
