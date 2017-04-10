package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.RemoteInvocationException;
import spread.SpreadException;

public interface Bank {
    boolean operation(int value) throws RemoteInvocationException, SpreadException, InterruptedException;
    int getBalance() throws RemoteInvocationException, SpreadException, InterruptedException;
}
