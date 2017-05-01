package pt.uminho.sdc.bank;

import pt.uminho.sdc.cs.SocketServer;

import java.io.IOException;

public class Servers {
    public static void main(String[] args) throws IOException {

        new Thread(){
            public void run(){
                try{
                    new SocketServer<>(4803, new ControllerImpl(), "srv1").serve();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            public void run(){
                try{
                    new SocketServer<>(4803, new ControllerImpl(), "srv2").serve();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
