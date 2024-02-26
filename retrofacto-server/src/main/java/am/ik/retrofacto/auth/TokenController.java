package am.ik.retrofacto.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import am.ik.retrofacto.retro.Board;
import am.ik.retrofacto.retro.RetroConstants;
import am.ik.retrofacto.retro.RetroService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

	private final RetroService retroService;

	private final JwtEncoder encoder;

	private final Clock clock;

	private static final int EXPIRY_HOUR = 3;

	public TokenController(RetroService retroService, JwtEncoder encoder, Clock clock) {
		this.retroService = retroService;
		this.encoder = encoder;
		this.clock = clock;
	}

	@PostMapping(path = "/token")
	public ResponseEntity<?> token(@RequestBody TokenRequest request) {
		Optional<Board> optionalBoard = this.retroService.findBySlug(request.slug());
		if (optionalBoard.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid slug."));
		}
		Board board = optionalBoard.get();
		if (!this.retroService.checkPassword(request.passphrase(), board)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid passphrase."));
		}
		Instant now = Instant.now(this.clock);
		JwtClaimsSet claims = JwtClaimsSet.builder()
			.issuedAt(now)
			// expires after the corresponding cookie expires
			.expiresAt(now.plus(EXPIRY_HOUR, ChronoUnit.HOURS).plusMillis(5))
			.issuer("retrofacto")
			.audience(List.of(request.slug()))
			.build();
		String jwt = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
		ResponseCookie cookie = ResponseCookie.from(RetroConstants.RETRO_COOKIE_PREFIX + request.slug(), jwt)
			.httpOnly(true)
			.maxAge(Duration.ofHours(EXPIRY_HOUR))
			.build();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
	}

	public record TokenRequest(String slug, String passphrase) {

	}

}
