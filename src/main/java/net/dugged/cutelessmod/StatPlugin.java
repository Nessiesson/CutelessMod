package net.dugged.cutelessmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatPlugin {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ExecutorService service = Executors.newCachedThreadPool();
    private DataOutputStream streamOut;
    private Socket socket;
    private int amount;

    public void connect() throws IOException {
        socket = new Socket("127.0.0.1", 8192);
        streamOut = new DataOutputStream(socket.getOutputStream());
        amount = 0;
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
            streamOut.close();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void sendStatIncrease(int count) {
        amount = count;
        service.submit(new StatPluginThread());
    }

    class StatPluginThread implements Runnable {
        public void run() {
            try {
                streamOut.writeUTF("increase stat: " + amount);
                streamOut.flush();
            } catch (IOException e) {
                LOGGER.info("An error occured while sending to the OBS-Plugin!");
            }
        }
    }
}
