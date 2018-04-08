package com.winning.mars_consumer.monitor.server;

import android.net.Uri;
import android.text.TextUtils;

import com.winning.mars_consumer.monitor.PresenterMapper;
import com.winning.mars_generator.utils.LogUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
            try{
                // set buffer area to output status
                buffer.flip();
                // convert message to UTF-8
                String receivedString= Charset.forName("UTF-8").newDecoder().decode(buffer).toString();
                // print to console
                System.out.println("get"+clientChannel.socket().getRemoteSocketAddress()+"'s information:"+receivedString);

                String pathAndParams = null;
                String[] requestMessage = receivedString.split("\r\n");
                for (String line : requestMessage){
                    if (line.startsWith("GET /")) {
                        int start = line.indexOf('/') + 1;
                        int end = line.indexOf(' ', start);
                        pathAndParams = line.substring(start, end);
                        break;
                    }
                }

                Uri uri = parseUri(pathAndParams);
                String data = PresenterMapper.getInstance().process(uri);
                if (null == data || "".equalsIgnoreCase(data)){
                    responseServerError(clientChannel);
                    return;
                }

                StringBuilder sendString = new StringBuilder();
                sendString.append("HTTP/1.0 200 OK\r\n");
                sendString.append("Content-Type:").append(prepareMimeType(uri.getPath())).append("\r\n");
                sendString.append("Content-Length:").append(data.length()).append("\r\n");
                sendString.append("\r\n");
                sendString.append(data);
                ByteBuffer bufferResponse = ByteBuffer.wrap(sendString.toString().getBytes());
                while (bufferResponse.hasRemaining()) {
                    clientChannel.write(bufferResponse);
                }
            }catch (Throwable e){
                LogUtil.d(this.getClass().getSimpleName(),"查找路由出错");
            }finally {
                clientChannel.close();
            }
        }
    }

    @Override
    public void handleWrite(SelectionKey key) throws IOException {

    }

    private void responseServerError(SocketChannel socketChannel) throws Throwable{
        ByteBuffer buffer = ByteBuffer.wrap("HTTP/1.0 500 Internal Server Error".getBytes("UTF-8"));
        socketChannel.write(buffer);
    }

    private Uri parseUri(String url) throws UnsupportedEncodingException {
        return Uri.parse(URLDecoder.decode(url, "UTF-8"));
    }

    private static String prepareMimeType(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        } else if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else {
            return "application/json";
        }
    }
}
