package pt.uminho.sdc.controller;

import pt.uminho.sdc.cs.RemoteInvocationException;
import spread.SpreadException;

public interface Controller {
    boolean setOccupied(int linha, int segmento) throws RemoteInvocationException, SpreadException, InterruptedException;
    boolean setAvailable(int linha, int segmento) throws RemoteInvocationException, SpreadException, InterruptedException;
    boolean requestEntry(int linha, int segmento) throws RemoteInvocationException, SpreadException, InterruptedException;
    
}
