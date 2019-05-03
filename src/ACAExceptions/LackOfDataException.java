package ACAExceptions;

/**
 * Informs that data cannot be acquired
 */
public class LackOfDataException extends Exception {
    public LackOfDataException(){super("There is no data at given time");}
}
