package com.devsuperior.dscatalog.services.exceptions;

public class ServiceDataBaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	//construtor passando argumento para construtor da super classe
	public ServiceDataBaseException(String msg) {
		super(msg);
	}

}
