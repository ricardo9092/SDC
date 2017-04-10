package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.Client;
import pt.uminho.sdc.cs.RemoteInvocationException;
import spread.SpreadException;

public class BankClient implements Bank {

    private final Client<Bank> client;

    public BankClient(Client<Bank> client) {
        this.client = client;
    }

    @Override
    public boolean operation(int value) throws RemoteInvocationException, SpreadException, InterruptedException {
        client.request(new BankOperationRequest(value));
        return client.request(new BankOperationRequest(value));
    }

    @Override
    public int getBalance() throws RemoteInvocationException, SpreadException, InterruptedException {
        return client.request(new BankBalanceRequest());
    }
}
