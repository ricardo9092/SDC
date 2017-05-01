package pt.uminho.sdc.cs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spread.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

public class SocketClient<T> implements Client<T> {
    private static Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private SpreadMessage sendMessage;
    private Reply rep;
    private int port;
    private int messageMark;
    private int currentMark;
    
    private SpreadConnection connection = new SpreadConnection();
    private SpreadGroup group = new SpreadGroup();
    
    private String clientName;

    ArrayList<SpreadMessage> theMessageList = new ArrayList<SpreadMessage>();
    
    public SocketClient(String server, int port, String clientName) throws IOException {
            	
    	this.clientName = clientName;
    	this.port=port;
    	this.messageMark = 0;
    	this.currentMark = -1;
        UUID idOne = UUID.randomUUID();
        clientName = clientName + idOne.toString().substring(idOne.toString().length() - 12);
    	System.out.println("Client Name = " + clientName + idOne.toString().substring(idOne.toString().length() - 12));
        //System.out.println("Creating SocketClient");

        try {
			connection.connect(InetAddress.getByName("localhost") , port, clientName, false, false);
            group.join(connection,clientName);
		} catch (SpreadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
    }
    
    
 /*   public synchronized <V> V request(Request<T,V> req) throws RemoteInvocationException, SpreadException, InterruptedException {
        currentMark = messageMark;
        boolean completed = false;
        sendMessage = new SpreadMessage();
        sendMessage.addGroup("groupServer");
        sendMessage.setReliable();
        System.out.println("The REQ is = " + req);
        req.setMessageMark(messageMark);
        messageMark = messageMark + 1;
        sendMessage.setObject(req);

        logger.info("The connection is = " + connection.isConnected());
        connection.multicast(sendMessage);

        //logger.info("message multicast");

        Reply r = null;
        SpreadMessage receivedMessage = new SpreadMessage();

        //logger.info("thread running");
        try {
            receivedMessage = connection.receive();
            System.out.println("Received From Server");
            //logger.info("message received");
        } catch (InterruptedIOException e) {
            e.printStackTrace();
        } catch (SpreadException e) {
            //logger.info("exception");
            e.printStackTrace();
        }

        try {
            r = (Reply) receivedMessage.getObject();
        } catch (SpreadException e) {
            e.printStackTrace();
        }

        System.out.println("Received Message Mark = " + r.getMessageMark() + " Current Mark = " + currentMark);
        setReply(r);

        while (rep == null) {
            wait();
        }


        if (rep instanceof ValueReply) {
            return ((ValueReply<V>) rep).getValue();
        } else {
            throw ((ErrorReply) rep).getException();
        }
    }

*/


    public synchronized <V> V request(Request<T,V> req) throws RemoteInvocationException, SpreadException, InterruptedException {
        int currentMark = messageMark;
        messageMark = messageMark + 1;
        boolean completed = false;
        sendMessage = new SpreadMessage();
        sendMessage.addGroup("groupServer");
        sendMessage.setReliable();
        System.out.println("The REQ is = " + req);
        req.setMessageMark(currentMark);
        sendMessage.setObject(req);

        logger.info("The connection is = " + connection.isConnected());
        connection.multicast(sendMessage);

        //logger.info("message multicast");

        //new Thread() {
            Reply r = null;
            SpreadMessage receivedMessage = new SpreadMessage();

            //public void run() {
                //logger.info("thread running");
                //while(true){
                    try {
                        receivedMessage = connection.receive();
                        System.out.println("Received From Server");
                        //logger.info("message received");
                    } catch (InterruptedIOException e) {
                        e.printStackTrace();
                    } catch (SpreadException e) {
                        //logger.info("exception");
                        e.printStackTrace();
                    }

                    try {
                        r = (Reply) receivedMessage.getObject();
                    } catch (SpreadException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Received Message Mark = " + r.getMessageMark() + " Current Mark = " + currentMark);
                    if (!(r.getMessageMark() < currentMark))
                        setReply(r);
                //}
            //}
        //}.start();

        while (rep == null) {
            wait();
        }


        if (rep instanceof ValueReply) {
            return ((ValueReply<V>) rep).getValue();
        } else {
            throw ((ErrorReply) rep).getException();
        }
    }


    public synchronized void setReply(Reply r){
            System.out.println("Setting the Reply for Mark = " + r.getMessageMark());
            rep = r;
            notifyAll();
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
