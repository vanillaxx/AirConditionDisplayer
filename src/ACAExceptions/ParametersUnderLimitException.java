package ACAExceptions;

/**
 * Informs about particular situation when none of parametres reached the limit value
 */
public class ParametersUnderLimitException extends Exception {
    public ParametersUnderLimitException(){super("No parameter has reached the limit");}
}
