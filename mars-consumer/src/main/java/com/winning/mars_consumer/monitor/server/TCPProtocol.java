package com.winning.mars_consumer.monitor.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created by yuzhijun on 2017/6/13.
 */
public interface TCPProtocol {
    /**
     * accept messages from SocketChannel
     * @param key
     * @throws IOException
     */
    void handleAccept(SelectionKey key) throws IOException;
    /**
     * read from SocketChannel
     * @param key
     * @throws IOException
     */
    void handleRead(SelectionKey key) throws IOException;
    /**
     * write messages to SocketChannel
     * @param key
     * @throws IOException
     */
    void handleWrite(SelectionKey key) throws IOException;

}
