package ACAExceptions;

/**
 * Informs that name of stations is wrong
 */
public class LackOfStationException extends Exception {
    public LackOfStationException(String nameOfStation) { super(nameOfStation + " was not recognized");}
}
