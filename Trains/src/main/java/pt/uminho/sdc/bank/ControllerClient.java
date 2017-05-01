package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.Client;
import pt.uminho.sdc.cs.RemoteInvocationException;
import spread.SpreadException;

public class ControllerClient implements Controller {

    private final Client<Controller> client;

    public ControllerClient(Client<Controller> client) {
        this.client = client;
    }

    @Override
    public boolean operation(int value) throws RemoteInvocationException, SpreadException, InterruptedException {
        client.request(new ReqOperation(value));
        return client.request(new ReqOperation(value));
    }

    @Override
    public int getBalance() throws RemoteInvocationException, SpreadException, InterruptedException {
        return client.request(new ReqBalance());
    }
    
    @Override
    public boolean requestEntry(int linha, int segmento)throws RemoteInvocationException, SpreadException, InterruptedException {
        return client.request(new ReqEntry(linha, segmento));
    }
    
    @Override
    public boolean setAvailable(int linha, int segmento)throws RemoteInvocationException, SpreadException, InterruptedException {
        return client.request(new ReqSetAvailable(linha, segmento));
    }

    @Override
    public boolean setOccupied(int linha, int segmento)throws RemoteInvocationException, SpreadException, InterruptedException {
        return client.request(new ReqSetOccupied(linha, segmento));
    }
}
