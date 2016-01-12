package net.ziahaqi.robomq;

import android.os.Handler;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by ziahaqi on 1/11/2016.
 */
public abstract class MQConnector {
    protected Channel mChannel = null;
    protected Connection mConnection;
    protected Handler mStateHandler;

    private boolean running;
    protected String host;
    protected String virtualHost;
    protected String username;
    protected String password;
    protected int port;
    private int requestTimeOut = 30 * 1000;
    private int requestHeartBeat = 30;

    public MQConnector(String host, String virtualHost, String username, String password, int port) {
        this.host = host;
        this.virtualHost = virtualHost;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    protected boolean isRunning() {
        return running;
    }

    protected abstract void createChannel();

    public void closeMQConnection() throws IOException, TimeoutException {
        running = false;
        if (mConnection != null) {
            mConnection.close();
        }
        if (mChannel != null) {

            mChannel.close();
            mChannel.abort();
        }
    }

    protected void createConnection() throws IOException, TimeoutException {
        if (!isConnected()) {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setUsername(username);
            connectionFactory.setPassword(password);
            connectionFactory.setVirtualHost(virtualHost);
            connectionFactory.setHost(host);
            connectionFactory.setPort(port);
            connectionFactory.setConnectionTimeout(this.requestTimeOut);
            connectionFactory.setRequestedHeartbeat(this.requestHeartBeat);
            mConnection = connectionFactory.newConnection();
            mConnection.addShutdownListener(createShutDownListener());
        }
    }

    protected abstract ShutdownListener createShutDownListener();

    protected void setConnectionTimeOut(int requestTimeOut){
        this.requestTimeOut = requestTimeOut;
    }

    protected void setRequestedHeartbeat(int requestHeartBeat){
        this.requestHeartBeat = requestHeartBeat;
    }

    public boolean isConnected() {
        if (mConnection != null && mConnection.isOpen()) {
            return true;
        }
        return false;
    }

    public boolean isChannelAvailable() {
        if (mChannel != null && mChannel.isOpen()) {
            return true;
        }
        return false;
    }


}
