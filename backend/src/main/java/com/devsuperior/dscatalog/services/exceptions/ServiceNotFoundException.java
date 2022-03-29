package com.devsuperior.dscatalog.services.exceptions;

public class ServiceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	//construtor passando argumento para construtor da super classe
	public ServiceNotFoundException(String msg) {
		super(msg);
	}

}
