package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.Request;
import spread.SpreadException;
import pt.uminho.sdc.cs.RemoteInvocationException;

public class ReqSetOccupied extends Request<Controller,Boolean> {
    private final int linha;
    private final int segmento;

    public ReqSetOccupied(int linha, int segmento) {
        this.linha = linha;
        this.segmento = segmento;
    }

    @Override
    public Boolean apply(Controller state) throws RemoteInvocationException, SpreadException, InterruptedException {
        return state.setOccupied(linha, segmento);
    }

    public String toString() {
        return "Occupied Request: Linha = "+linha+ " Segmento = "+segmento;
    }

}
