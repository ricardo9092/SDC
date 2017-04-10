package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.SocketClient;

import java.io.IOException;

public class TestRemote {
    public static void main(String[] args) throws Exception {
        new BankTester(
                () -> new BankClient(
                    //new SocketClient<Bank>("localhost", 12345, 12346)
                	new SocketClient<Bank>("localhost", 4803, "client")
                ), args)
            .test();
    }
}
