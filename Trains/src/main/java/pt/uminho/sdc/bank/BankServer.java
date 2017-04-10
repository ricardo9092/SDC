package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.SocketServer;

import java.io.IOException;

public class BankServer {
    public static void main(String[] args) throws IOException {
        //new SocketServer<>(12346, new BankImpl()).serve();
        //new SocketServer<>(12345, new BankImpl()).serve();
        new Thread(){
            public void run(){
                try{
                    //new SocketServer<>(12345, new BankImpl()).serve();
                    new SocketServer<>(4803, new BankImpl(), "srv1").serve();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            public void run(){
                try{
                    new SocketServer<>(4803, new BankImpl(), "srv2").serve();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
