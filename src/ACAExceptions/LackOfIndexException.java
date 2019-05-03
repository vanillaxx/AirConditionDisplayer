package ACAExceptions;

/**
 * Informs that such a station does not have information about air condition index
 */
public class LackOfIndexException extends Exception {
    public LackOfIndexException(){super("There is no air condition index for that station");}
}
