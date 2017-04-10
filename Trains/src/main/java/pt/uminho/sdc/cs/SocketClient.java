package pt.uminho.sdc.cs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spread.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;

public class SocketClient<T> implements Client<T> {
    private static Logger logger = LoggerFactory.getLogger(SocketClient.class);

    /*private final Socket socket;
    private final Socket socketServer2;
    private final ObjectOutputStream oos;
    private final ObjectOutputStream oos2;
    private final ObjectInputStream ois;
    private final ObjectInputStream ois2;*/
    private Reply rep;
    
    private SpreadConnection connection = new SpreadConnection();
    
    private SpreadGroup group;
    
    private SpreadMessage sendMessage;
    
    private String clientName;

   /* public SocketClient(String server, int port, int port2) throws IOException {
        
        logger.debug("client connecting to server 1: {}:{}", server, port);
        this.socket = new Socket(server, port);
        logger.debug("client connecting to server 2: {}:{}", server, port2);
        this.socketServer2 = new Socket(server, port2);
        oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        oos.flush();
        ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        logger.info("client connected to server 1: {}", socket);
        oos2 = new ObjectOutputStream(new BufferedOutputStream(socketServer2.getOutputStream()));
        oos2.flush();
        ois2 = new ObjectInputStream(new BufferedInputStream(socketServer2.getInputStream()));
        logger.info("client connected to server 2: {}", socketServer2);
        
        
    } */
    ArrayList<SpreadMessage> theMessageList = new ArrayList<SpreadMessage>();
    
    public SocketClient(String server, int port, String clientName) throws IOException {
            	
    	this.clientName = clientName;
    	
        try {
			connection.connect(InetAddress.getByName("localhost") , port, clientName, false, false);
		} catch (SpreadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*
        group = new SpreadGroup();
        
        try {
			group.join(connection, "groupClient");
		} catch (SpreadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        */
        sendMessage = new SpreadMessage();
        
    }
    
    
    public synchronized <V> V request(Request<T,V> req) throws RemoteInvocationException, SpreadException, InterruptedException {
        
        
        sendMessage.setObject(req);
        sendMessage.addGroup("groupServer");
        sendMessage.setReliable();
        
        connection.multicast(sendMessage);
        
        new Thread(){
            private Reply r;
            private SpreadMessage receivedMessage = new SpreadMessage();
            
            public void run(){
                try {
					receivedMessage = connection.receive();
					theMessageList.add(receivedMessage);
				} catch (InterruptedIOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SpreadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                try {
					System.out.println("Client Received = " + receivedMessage.getObject());

					r = (Reply) receivedMessage.getObject();
                    System.out.println("The reply is after = " + r);
				} catch (SpreadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                                
                setReply(r);
            }
        }.start();
        
        
            
        while(rep == null){
            wait();
        }
        
        
        if (rep instanceof ValueReply) {
            return ((ValueReply<V>) rep).getValue();
        } else {
            throw ((ErrorReply)rep).getException();
        }
    }

    public synchronized void setReply(Reply r){
        System.out.println("The reply is r =  " + r);
        if (rep == null){
            rep = r;
            System.out.println("The reply is " + rep);
            notifyAll();
        }
    }

    
    public void close() throws IOException, SpreadException {
        connection.disconnect();
    }

    /*
    public synchronized void setReply(Reply r){
        if (rep == null){
            rep = r;
            notifyAll();
        }
    }
    

    public synchronized <V> V request(Request<T,V> req) throws RemoteInvocationException {
        //Reply rep = null;
        rep = null;
        try {
            oos.writeObject(req);
            oos.flush();
            logger.debug("sending request to server 1: {}", req);
            oos2.writeObject(req);
            oos2.flush();
            logger.debug("sending request to server 2: {}", req);
           
            new Thread(){
                private Reply r;
                private Logger logger = LoggerFactory.getLogger(SocketClient.class);
                public void run(){
                    try{
                        r = (Reply) ois.readObject();
                        if (rep == null)
                            setReply(r);
                        logger.debug("received reply from server 1: {}", rep);
                    }catch(Exception e){
                        logger.error("error talking to server 1", e);
                        e.printStackTrace();
                    }
                }
            }.start();
            
            new Thread(){
                private Reply r;
                private Logger logger = LoggerFactory.getLogger(SocketClient.class);
                public void run(){
                    try{
                        r = (Reply) ois2.readObject();
                        if (rep == null)
                            setReply(r);
                        logger.debug("received reply from server 2: {}", rep);
                    }catch(Exception e){
                        logger.error("error talking to server 2", e);
                        e.printStackTrace();
                    }
                }
            }.start();
            
            while(rep == null){
                wait();
            }
            
        } catch(Exception e) {
            logger.error("error talking to server", e);
            throw new ClientSideException(e);
        }

        if (rep instanceof ValueReply) {
            return ((ValueReply<V>) rep).getValue();
        } else {
            throw ((ErrorReply)rep).getException();
        }
    }
    
     
     
    public void run(){
        
    }

    public void close() throws IOException {
        socket.close();
        logger.info("client disconnected from server 1: {}", socket);
        socketServer2.close();
        logger.info("client disconnected from server 2: {}", socketServer2);
    }
     
     */
}
