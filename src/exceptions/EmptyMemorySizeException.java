package exceptions;

public class EmptyMemorySizeException extends Exception{

    public EmptyMemorySizeException(){
        super("Debe ingresar un tama�o de memoria");
    }
}
