package com.winning.mars_consumer.monitor.server;

import com.winning.mars_consumer.utils.DefaultPoolExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * port distribution: 0~65535,0 ~ 1024 has already occupied
 * Created by yuzhijun on 2018/4/2.
 */
public class MarsSocketServer{
    private static MarsSocketServer mInstance;
    private  Selector selector;
    private ServerSocketChannel listenerChannel;
    // buffer size
    private static final int BufferSize = 1024;
    private static final int DEFAULT_PORT = 5666;
    private MarsSocketServer(){
    }

    public static MarsSocketServer getInstance(){
        if (null == mInstance){
            synchronized (MarsSocketServer.class){
                if (null == mInstance){
                    mInstance = new MarsSocketServer();
                }
            }
        }

        return mInstance;
    }

    public void startServer(){
        DefaultPoolExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                startInternalServer();
            }
        });
    }

    private synchronized void startInternalServer(){
        try{
            // create selector
            selector = Selector.open();
            // open listener channel
            listenerChannel = ServerSocketChannel.open();
            // set no block
            listenerChannel.configureBlocking(false);
            // bind
            listenerChannel.socket().bind(new InetSocketAddress(socketPort()));
            // bind selector to listener channel when in no block mode, and specify the accept operation
            listenerChannel.register(selector, SelectionKey.OP_ACCEPT);
            // create one implementation class
            TCPProtocol protocol = new TCPProtocolImpl(BufferSize);
            while (selector.select() > 0){
                //get iterator, .selectedKeys() contains a SelectionKey ready for a certain I/O operation
                Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
                while (keyIter.hasNext()){
                    SelectionKey key = keyIter.next();
                    try{
                        if(key.isAcceptable()){
                            // when client connected
                            protocol.handleAccept(key);
                        }
                        if(key.isReadable()){
                            // read from client
                            protocol.handleRead(key);
                        }
                        if(key.isValid() && key.isWritable()){
                            // write to client
                            protocol.handleWrite(key);
                        }
                    }
                    catch(IOException ex){
                        // when exception occurs
                        keyIter.remove();
                        continue;
                    }
                    // remove key already operated
                    keyIter.remove();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (null != selector){
                    selector.close();
                }
                if (null != listenerChannel){
                    listenerChannel.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public synchronized void stopServer(){

    }

    private int socketPort(){
        return DEFAULT_PORT;
    }
}
