package pt.uminho.sdc.controller;

import pt.uminho.sdc.cs.Request;
import spread.SpreadException;
import pt.uminho.sdc.cs.RemoteInvocationException;

public class ReqEntry extends Request<Controller,Boolean> {
    private final int linha;
    private final int segmento;

    public ReqEntry(int linha, int segmento) {
        this.linha = linha;
        this.segmento = segmento;
    }

    @Override
    public Boolean apply(Controller state) throws RemoteInvocationException, SpreadException, InterruptedException {
        return state.requestEntry(linha, segmento);
    }

    public String toString() {
        return "Entry Request: Linha = "+linha+ " Segmento = "+segmento;
    }

}
