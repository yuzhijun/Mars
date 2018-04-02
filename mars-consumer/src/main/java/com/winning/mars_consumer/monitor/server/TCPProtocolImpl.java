package com.winning.mars_consumer.monitor.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * TCPProtocol implementation
 * Created by yuzhijun on 2017/6/13.
 */
public class TCPProtocolImpl implements TCPProtocol {
    private int bufferSize;

    public  TCPProtocolImpl(int bufferSize){
        this.bufferSize=bufferSize;
    }

    @Override
    public void handleAccept(SelectionKey key) throws IOException {
        SocketChannel clientChannel=((ServerSocketChannel)key.channel()).accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
    }

    @Override
    public void handleRead(SelectionKey key) throws IOException {
        // get the channel for communication
        SocketChannel clientChannel = (SocketChannel)key.channel();

        // get buffer area
        ByteBuffer buffer=(ByteBuffer)key.attachment();
        buffer.clear();

        // get message byte
        long bytesRead=clientChannel.read(buffer);

        if(bytesRead==-1){
            // read nothing
            clientChannel.close();
        }
        else{
            // set buffer area to output status
            buffer.flip();
            // convert message to UTF-8
            String receivedString= Charset.forName("UTF-8").newDecoder().decode(buffer).toString();
            // print to console
            System.out.println("get"+clientChannel.socket().getRemoteSocketAddress()+"'s information:"+receivedString);
            if (null != mSocketCallBack){
                // prepare message
                String sendString = mSocketCallBack.popSocketData(receivedString);
                buffer = ByteBuffer.wrap(sendString.getBytes("UTF-8"));
                clientChannel.write(buffer);
            }
            // set read or write for next operation
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    @Override
    public void handleWrite(SelectionKey key) throws IOException {
    }

    private static ISocketCallBack mSocketCallBack;
    public interface ISocketCallBack{
        String popSocketData(String content);
    }

    public static void regisiterSocketCallBack(ISocketCallBack socketCallBack){
        mSocketCallBack = socketCallBack;
    }

    public static void unRegisterSocketCallBack(){
        if (null != mSocketCallBack){
            mSocketCallBack = null;
        }
    }
}
