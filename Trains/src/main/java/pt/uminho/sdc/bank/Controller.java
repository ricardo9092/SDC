package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.RemoteInvocationException;
import spread.SpreadException;

public interface Controller {
    boolean operation(int value) throws RemoteInvocationException, SpreadException, InterruptedException;
    int getBalance() throws RemoteInvocationException, SpreadException, InterruptedException;
    boolean setOccupied(int linha, int segmento) throws RemoteInvocationException, SpreadException, InterruptedException;
    boolean setAvailable(int linha, int segmento) throws RemoteInvocationException, SpreadException, InterruptedException;
    boolean requestEntry(int linha, int segmento) throws RemoteInvocationException, SpreadException, InterruptedException;
    
}
