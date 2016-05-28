package com.github.zachdeibert.javasandbox;

final class IllegalReferenceException extends RuntimeException {
	private static final long serialVersionUID = 7108838228529658596L;

	public IllegalReferenceException() {
	}

	public IllegalReferenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalReferenceException(String message) {
		super(message);
	}

	public IllegalReferenceException(Throwable cause) {
		super(cause);
	}
}
