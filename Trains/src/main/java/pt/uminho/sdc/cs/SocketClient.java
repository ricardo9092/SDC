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

    public SocketClient(String server, int port, String clientName) throws IOException {
            	
    	this.clientName = clientName;
    	this.port=port;
    	this.messageMark = 0;
    	this.currentMark = -1;
        UUID idOne = UUID.randomUUID();
        clientName = clientName + idOne.toString().substring(idOne.toString().length() - 12);

        try {
			connection.connect(InetAddress.getByName("localhost") , port, clientName, false, false);
            group.join(connection,clientName);
		} catch (SpreadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
    }


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

        connection.multicast(sendMessage);

        Reply r = null;
        SpreadMessage receivedMessage = new SpreadMessage();

        try {
            receivedMessage = connection.receive();
        } catch (InterruptedIOException e) {
            e.printStackTrace();
        } catch (SpreadException e) {
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
            rep = r;
            notifyAll();
    }

    
    public void close() throws IOException, SpreadException {
        connection.disconnect();
    }

}
