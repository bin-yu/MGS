package org.yyy.doorproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        query(args[0], Integer.parseInt(args[1]));
    }

    private static void query(String host, int port) throws IOException {
        InetSocketAddress isa = new InetSocketAddress(InetAddress.getByName(host), port);

        Socket s = new Socket();

        try {
            s.connect(isa);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"))) {
                String msg = in.readLine();
                System.out.println("Message received : " + msg);
                String sendMsg = "I'm proxy.";
                System.out.println("Sending message: " + sendMsg);
                try (PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
                    out.println(sendMsg);
                }
            }

        } finally {
            if (s != null)
                s.close();
        }
    }
}
