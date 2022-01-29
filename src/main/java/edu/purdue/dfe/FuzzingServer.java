package edu.purdue.dfe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FuzzingServer {
    private static final String ADDRESS = "localhost";

    private static final int PORT = 6324;

    public static void main(String[] args) {
        new FuzzingServer().start();
    }

    public void start() {
        try {
            final ServerSocket socket = new ServerSocket(PORT, 0, InetAddress.getByName(ADDRESS));
            while (true) {
                final Handler handler = new Handler(socket.accept());
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler implements Runnable {

        private final Socket socket;

        private final int maxn = 10240;

        private byte[] buffer;

        Handler(final Socket socket) throws IOException {
            this.socket = socket;
            buffer = new byte[maxn];
        }

        @Override
        public void run() {
            try {
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                System.out.println("Server send " + new String("test server".getBytes()));
                out.write("test server".getBytes());
                in.read(buffer);
                System.out.println("Server get: " + new String(buffer));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

    }

}
