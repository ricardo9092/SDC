package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.Request;
import spread.SpreadException;
import pt.uminho.sdc.cs.RemoteInvocationException;

public class ReqBalance extends Request<Controller,Integer> {

    @Override
    public Integer apply(Controller state) throws RemoteInvocationException, SpreadException, InterruptedException {
        return state.getBalance();
    }

    public String toString() {
        return "Balance Request ";
    }
}
