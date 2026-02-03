package pfe.example.autehntification_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class RessourceNotExist extends RuntimeException{

	public RessourceNotExist(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	
}
