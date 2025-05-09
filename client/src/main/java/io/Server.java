package io;

import commands.Exit;
import exceptions.ServerDisconnect;
import storage.Logging;
import storage.Request;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

/**
 * Handles low-level communication with the remote server over UDP.
 * <p>
 * Serializes {@link Request} objects, sends them to the configured server host and port,
 * waits for a response within a timeout, and returns the decoded UTF-8 string.
 * </p>
 * <p>
 * On send or receive errors, logs exceptions and throws {@link ServerDisconnect}.
 * Exits the client on timeout or unrecoverable error.
 * </p>
 */
public class Server {

    /** Default server hostname. */
    private static final String SERVER_HOST = "127.0.0.1"; // helios.cs.ifmo.ru    127.0.0.1
    /** Default server port number. */
    private static final int SERVER_PORT = 6600;

    /**
     * Returns the configured server hostname.
     *
     * @return the server host name (never null)
     */
    public static String getServerHost() {
        return SERVER_HOST;
    }

    /**
     * Returns the configured server port.
     *
     * @return the server port number
     */
    public static int getServerPort() {
        return SERVER_PORT;
    }

    /**
     * Sends a {@link Request} to the server and receives a string response.
     * <p>
     * Opens a non-blocking UDP channel, serializes the request, and writes it.
     * Waits up to 3 seconds for a response, polling every 10ms. On timeout,
     * prints an error, exits via {@link Exit}, and throws {@link ServerDisconnect}.
     * </p>
     *
     * @param request the request to send (must be serializable)
     * @return the UTF-8 decoded server response
     * @throws ServerDisconnect if unable to send, receive, or on timeout
     */
    public static String interaction(Request<?> request) throws ServerDisconnect {
        try (DatagramChannel client = DatagramChannel.open()) {
            client.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            client.configureBlocking(false);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {
                oout.writeObject(request);
            }
            byte[] bytes = bout.toByteArray();

            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            client.write(buffer);

            ByteBuffer recv = ByteBuffer.allocate(4096);
            long deadline = System.currentTimeMillis() + 3000;
            while (recv.position() == 0) {
                if (System.currentTimeMillis() > deadline) {
                    System.out.println("Server unavailable.");
                    Exit.exit();
                    throw new ServerDisconnect("Response timeout");
                }
                Thread.sleep(5);
                client.read(recv);
            }
            recv.flip();
            return StandardCharsets.UTF_8.decode(recv).toString();

        } catch (ServerDisconnect e) {
            throw e;
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        throw new ServerDisconnect("Communication failure");
    }
}
