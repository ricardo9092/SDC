package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.Request;
import spread.SpreadException;
import pt.uminho.sdc.cs.RemoteInvocationException;

public class ReqOperation extends Request<Controller,Boolean> {
    private final int value;

    public ReqOperation(int value) {
        this.value = value;
    }

    @Override
    public Boolean apply(Controller state) throws RemoteInvocationException, SpreadException, InterruptedException {
        return state.operation(value);
    }

    public String toString() {
        return "Operatiton Request: value = "+value ;
    }

}
