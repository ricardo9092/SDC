package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.SocketClient;

public class TrainsTest {
    public static void main(String[] args) throws Exception {
        new Tester(
                () -> new ControllerClient(
                    //new SocketClient<Controller>("localhost", 12345, 12346)
                	new SocketClient<Controller>("localhost", 4803, "client")
                ), args)
            .test();
    }
}
