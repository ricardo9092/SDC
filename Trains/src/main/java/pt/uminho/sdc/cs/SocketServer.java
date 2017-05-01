package pt.uminho.sdc.cs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spread.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;

public class SocketServer<T> {

    private static Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final T state;
    private final int port;
    private final String serverName;
    
    private SpreadConnection connection;
    
    private SpreadMessage receivedMessage;
    
    private SpreadMessage sendMessage;
    
    private SpreadGroup group;

    
    public SocketServer(int port, T state, String serverName) throws IOException, SpreadException {

    	this.port = port;
    	
        this.serverName = serverName;
                
    	connection = new SpreadConnection();
                
        receivedMessage = new SpreadMessage();
        
        sendMessage = new SpreadMessage();
        
        connection.connect(InetAddress.getByName("localhost") , port, serverName, false, false);
                
        group = new SpreadGroup();
        
        group.join(connection, "groupServer");
        
        this.state = state;
        

    }
    
    public void serve() throws IOException {
        System.out.println("Server starting at port {"+port+"}");
        
        Worker worker = new Worker();
        
        worker.start();
    }
    
    
    private class Worker extends Thread {
        public Worker (){}
        
        public void run(){
            while (true) {
            	Reply rep = null;
                SpreadGroup g = null;
                try {
					receivedMessage = connection.receive();
					g = receivedMessage.getSender();
                    Request<T, ?> req = (Request<T,?>) receivedMessage.getObject();
                    //System.out.println("Request Received = " + receivedMessage.getObject() + " Mark = " + req.getMessageMark() + " on Server = " + serverName);

                    Thread t = new Thread(new MessageTreater(g, req));
                    t.start();

				} catch (InterruptedIOException e1) {
					e1.printStackTrace();
				} catch (SpreadException e1) {
					e1.printStackTrace();
				}
            } 
        }
    }

    class MessageTreater implements Runnable {
        SpreadGroup g;
        Reply rep = null;
        Request req = null;
        SpreadMessage sendMessage;


        MessageTreater(SpreadGroup group, Request request) { g = group; req = request; sendMessage = new SpreadMessage();}

        public void run(){
            while(rep == null || rep.toString().substring(7).equals("false")){

                try {
                    rep = new ValueReply<>(req.apply(state));
                    rep.setMessageMark(req.getMessageMark());
                } catch(RemoteInvocationException e) {
                    rep = new ErrorReply(e);
                } catch (Exception e) {
                    rep = new ErrorReply(new ServerSideException(e));
                }
            }

            try {
                sendMessage.setObject(rep);
                sendMessage.addGroup(g);
                sendMessage.setReliable();
                connection.multicast(sendMessage);

            } catch (SpreadException e) {
                e.printStackTrace();
            }
        }
    }
}
