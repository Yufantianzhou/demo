package com.example.demo.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpStream {
    private static final int DEFAULT_TIMEOUT = 5000;
    private final Socket socket;
    @SuppressWarnings("unused")
    private int timeout;


    public TcpStream(String ip, int port, int timeout) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port), timeout);
    }

    public TcpStream(String ip, int port) throws IOException {
        this(ip, port, DEFAULT_TIMEOUT);
    }

    public String sendAndReceive(byte[] msg) throws IOException {
        socket.getOutputStream().write(msg);
        return new String(socket.getInputStream().readAllBytes());
    }

    public static String sendAndReceive(String ip, int port, int timeout, byte[] msg) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.getOutputStream().write(msg);
            byte[] bytes = socket.getInputStream().readAllBytes();
            return new String(bytes);
        }
    }
}
