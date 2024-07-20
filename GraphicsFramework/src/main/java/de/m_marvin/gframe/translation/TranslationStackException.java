package de.m_marvin.gframe.translation;

/**
 * Signals that the pose/matrix stack of an rendering process got not cleared correctly.
 * A stack is cleared if the number of push class is equal to the number of pop calls.
 */
public class TranslationStackException extends RuntimeException {
	
    /**
	 * 
	 */
    @java.io.Serial
	private static final long serialVersionUID = 3149290208004082763L;

	public TranslationStackException() {
        super();
    }

    public TranslationStackException(String s) {
        super(s);
    }

    public TranslationStackException(String message, Throwable cause) {
        super(message, cause);
    }

    public TranslationStackException(Throwable cause) {
        super(cause);
    }

}
