package pfe.example.etudiantservice.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalArticleExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Erreur> traiterArticleIntrouvableException(
            ResourceNotFoundException e)
	{
		Erreur erreur= new Erreur(LocalDateTime.now(), e.getMessage(),
				404);
		return new ResponseEntity<Erreur>(erreur, HttpStatus.NOT_FOUND);
	}
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request){
		Map<String, String> map= new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(
				f->map.put(f.getField(),f.getDefaultMessage()));
		Erreur erreur= new Erreur(LocalDateTime.now(), ex.getMessage(),
				map,400);
		return new ResponseEntity<Object>(erreur, HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Erreur> traiterBDException(
			DataIntegrityViolationException e)
	{
		Erreur erreur= new Erreur(LocalDateTime.now(), e.getMessage(),
				409);
		return new ResponseEntity<Erreur>(erreur, HttpStatus.CONFLICT);
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Erreur> traiterException(
			Exception e)
	{
		Erreur erreur= new Erreur(LocalDateTime.now(),"Une erreur est survenue",
				500);
		return new ResponseEntity<Erreur>(erreur, HttpStatus.CONFLICT);
	}
}
