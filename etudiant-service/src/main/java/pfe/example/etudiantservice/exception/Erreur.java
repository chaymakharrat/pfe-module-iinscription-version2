package pfe.example.etudiantservice.exception;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Erreur {    
	@NonNull
	private LocalDateTime timestamp;
	@NonNull
	private String message;
	private Map<String, String> details;
	@NonNull
	private Integer status;}//fin classe Erreur
