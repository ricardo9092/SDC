package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.Request;
import spread.SpreadException;
import pt.uminho.sdc.cs.RemoteInvocationException;

public class BankSetOccupied extends Request<Bank,Boolean> {
    private final int linha;
    private final int segmento;

    public BankSetOccupied(int linha, int segmento) {
        this.linha = linha;
        this.segmento = segmento;
    }

    @Override
    public Boolean apply(Bank state) throws RemoteInvocationException, SpreadException, InterruptedException {
        return state.setOccupied(linha, segmento);
    }

    public String toString() {
        return "Entry Request: Linha = "+linha+ " Segmento = "+segmento;
    }
}
