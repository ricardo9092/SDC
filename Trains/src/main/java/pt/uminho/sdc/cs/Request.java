package pt.uminho.sdc.cs;

import spread.SpreadException;

public abstract class Request<T,V> extends Message {
    public abstract V apply(T state) throws RemoteInvocationException, SpreadException, InterruptedException;
}
