package pt.uminho.sdc.controller;

import pt.uminho.sdc.cs.Request;
import spread.SpreadException;
import pt.uminho.sdc.cs.RemoteInvocationException;

public class ReqSetAvailable extends Request<Controller,Boolean> {
    private final int linha;
    private final int segmento;

    public ReqSetAvailable(int linha, int segmento) {
        this.linha = linha;
        this.segmento = segmento;
    }

    @Override
    public Boolean apply(Controller state) throws RemoteInvocationException, SpreadException, InterruptedException {
        return state.setAvailable(linha, segmento);
    }

    public String toString() {
        return "Available Request: Linha = "+linha+ " Segmento = "+segmento;
    }
}
