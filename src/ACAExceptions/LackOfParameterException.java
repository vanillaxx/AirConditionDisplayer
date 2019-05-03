package ACAExceptions;

/**
 * Informs about particular situation when given station does not measure particular parameter
 */
public class LackOfParameterException extends Exception{
    public LackOfParameterException(String nameOfParameter){super(nameOfParameter + " is not included in the station");}
}
