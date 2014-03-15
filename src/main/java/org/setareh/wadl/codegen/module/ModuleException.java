package org.setareh.wadl.codegen.module;

/**
 * Exception may thrown by client module
 * 
 * @author bulldog
 *
 */
public class ModuleException extends Exception {
	private static final long serialVersionUID = 1L;

	public ModuleException() {
	}

	public ModuleException(String message) {
		super(message);
	}

	public ModuleException(Throwable cause) {
		super(cause);
	}

	public ModuleException(String message, Throwable cause) {
		super(message, cause);
	}
}
