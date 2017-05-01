package pt.uminho.sdc.controller;

import pt.uminho.sdc.cs.Client;
import pt.uminho.sdc.cs.RemoteInvocationException;
import spread.SpreadException;

public class ControllerClient implements Controller {

    private final Client<Controller> client;

    public ControllerClient(Client<Controller> client) {
        this.client = client;
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
