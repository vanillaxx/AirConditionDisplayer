package ACAExceptions;

/**
 * Exception to inform about an error if given start time is later then finish time
 */
public class IncorrectPeriodException extends Exception{
    public IncorrectPeriodException(){super("Finish date must be later than start date!");}
}
