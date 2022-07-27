package io.github.aliakseikaraliou.provideable;

public class ProvideableException extends Exception{
	public ProvideableException(String message) {
		super(message);
	}

	public ProvideableException(Throwable cause) {
		super(cause);
	}
}
