package pfe.example.autehntification_service.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pfe.example.autehntification_service.entities.Utilisateur;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class SecurityControler {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtEncoder jwtEncoder;
	@GetMapping("/profile")
	public Authentication authentication(Authentication authentication)
	{
		return authentication;
		
	}
	// on arrive dans cette etape a uthentifier le user
	@PostMapping("/login")
	public Map<String,String> login(@RequestBody Utilisateur user)
	{
		Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(),user.getPassword()));
		Instant instant=Instant.now();
		String scope=authentication.getAuthorities().stream().map(a->a.getAuthority()).collect(Collectors.joining(" "));
		JwtClaimsSet jwtClaimsSet=JwtClaimsSet.builder()
				.issuedAt(instant)
				.expiresAt(instant.plus(10,ChronoUnit.MINUTES))
				.subject(user.getLogin())
				.claim("scope", scope)
				.build();
		JwtEncoderParameters parameters = JwtEncoderParameters.from(
			    JwsHeader.with(MacAlgorithm.HS256).build(),
			    jwtClaimsSet
			);
		String jwt=jwtEncoder.encode(parameters).getTokenValue();
		return Map.of("access-token",jwt);
	}

}
