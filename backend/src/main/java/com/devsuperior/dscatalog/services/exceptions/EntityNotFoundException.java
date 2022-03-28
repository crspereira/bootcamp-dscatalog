package com.devsuperior.dscatalog.services.exceptions;

public class EntityNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	//construtor passando argumento para construtor da super classe
	public EntityNotFoundException(String msg) {
		super(msg);
	}

}
