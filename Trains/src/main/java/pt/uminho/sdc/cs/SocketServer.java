package pt.uminho.sdc.cs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spread.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

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
        logger.info("server starting at port {}", port);
        
        Worker worker = new Worker();
        
        worker.start();
    }
    
    
    private class Worker extends Thread {
        public Worker (){
            
        }
        
        public void run(){
            while (true) {
                System.out.println("WHILE");
            	Reply rep = null;
                SpreadGroup g = null;
                try {
					receivedMessage = connection.receive();
					System.out.println("Received Something");
					g = receivedMessage.getSender();
                    Request<T, ?> req = (Request<T,?>) receivedMessage.getObject();
                    System.out.println("Request Received = " + receivedMessage.getObject() + " Mark = " + req.getMessageMark());

                    //Reply rep = null;

                    while(rep == null || rep.toString().substring(7).equals("false")){

                        try {
                            rep = new ValueReply<>(req.apply(state));
                            rep.setMessageMark(req.getMessageMark());
                            if(rep != null)
                                System.out.println("REP STRING = " + rep.toString());
                            //System.out.println("Testing REP" + rep.toString().substring(7));
                            //System.out.println("The reply should be = " + rep);
                            //logger.trace("current state: {}", state);
                        } catch(RemoteInvocationException e) {
                            rep = new ErrorReply(e);
                        } catch (Exception e) {
                            //logger.warn("unexpected application exception", e);
                            rep = new ErrorReply(new ServerSideException(e));
                        }
                    }
				} catch (InterruptedIOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SpreadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
                try {
                    System.out.println("BEFORE SENDING THE MESSAGE");
                    sendMessage.setObject(rep);
	                sendMessage.addGroup(g);
	                sendMessage.setReliable();
					connection.multicast(sendMessage);
                    System.out.println("AFTER SENDING THE MESSAGE");
                    //System.out.println("Trying to send the message to the Client = " + sendMessage.getObject());

				} catch (SpreadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

               System.out.println("Should Stay in WHILE");
                
			/*	Request<T, ?> req = null;

				try {
					System.out.println("Received Request = " + receivedMessage.getObject());
					req = (Request<T,?>) receivedMessage.getObject();
				} catch (SpreadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
                logger.debug("received request: {}", req);
                Reply rep = null;
                try {
                    rep = new ValueReply<>(req.apply(state));
                    logger.trace("current state: {}", state);
                } catch(RemoteInvocationException e) {
                    rep = new ErrorReply(e);
                } catch (Exception e) {
                    logger.warn("unexpected application exception", e);
                    rep = new ErrorReply(new ServerSideException(e));
                }
                logger.debug("sending reply: {}", rep);
                
                try {
					sendMessage.setObject(rep);
				} catch (SpreadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                sendMessage.addGroup("group");
                sendMessage.setReliable();
                
                try {
					connection.multicast(sendMessage);
				} catch (SpreadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
            } 
        }
    }


   /* public SocketServer(int port, T state) throws IOException {
        this.ssocket = new ServerSocket(port);
        this.port = ssocket.getLocalPort();
        this.state = state;
    }

    public void serve() throws IOException {
        logger.info("server starting at port {}", port);
        try {
            while (true)
                new Worker(ssocket.accept()).start();
        } finally {
            logger.info("server at port {} stopped", port);
        }
    }

    private class Worker extends Thread {
        private final Socket socket;

        public Worker(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                logger.info("client connected: {}", socket);

                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                oos.flush();
                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

                while (true) {
                    Request<T,?> req = (Request<T,?>) ois.readObject();
                    logger.debug("received request: {}", req);
                    Reply rep = null;
                    try {
                        rep = new ValueReply<>(req.apply(state));
                        logger.trace("current state: {}", state);
                    } catch(RemoteInvocationException e) {
                        rep = new ErrorReply(e);
                    } catch (Exception e) {
                        logger.warn("unexpected application exception", e);
                        rep = new ErrorReply(new ServerSideException(e));
                    }
                    logger.debug("sending reply: {}", rep);
                    oos.writeObject(rep);
                    oos.flush();
                }

            } catch(EOFException e) {
                logger.info("client disconnected: {}", socket);
            } catch (IOException | ClassNotFoundException e) {
                logger.error("error reading request, closing connection", e);
            }

            try {
                socket.close();
            } catch (IOException e) {
                logger.error("error closing connection", e);
            }
        }
    }

    public void close() throws IOException {
        logger.info("closing server at port {}", port);
        ssocket.close();
    }
    
    */
}
