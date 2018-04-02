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
        // 获得与客户端通信的信道
        SocketChannel clientChannel = (SocketChannel)key.channel();

        // 得到并清空缓冲区
        ByteBuffer buffer=(ByteBuffer)key.attachment();
        buffer.clear();

        // 读取信息获得读取的字节数
        long bytesRead=clientChannel.read(buffer);

        if(bytesRead==-1){
            // 没有读取到内容的情况
            clientChannel.close();
        }
        else{
            // 将缓冲区准备为数据传出状态
            buffer.flip();
            // 将字节转化为为UTF-16的字符串
            String receivedString= Charset.forName("UTF-8").newDecoder().decode(buffer).toString();
            // 控制台打印出来
            System.out.println("get"+clientChannel.socket().getRemoteSocketAddress()+"'s information:"+receivedString);
            if (null != mSocketCallBack){
                // 准备发送的文本
                String sendString = mSocketCallBack.popSocketData(receivedString);
                buffer = ByteBuffer.wrap(sendString.getBytes("UTF-8"));
                clientChannel.write(buffer);
            }
            // 设置为下一次读取或是写入做准备
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
