package pt.uminho.sdc.cs;

import spread.SpreadException;

public interface Client<T> {
    <V> V request(Request<T,V> req) throws RemoteInvocationException, SpreadException, InterruptedException;
}
