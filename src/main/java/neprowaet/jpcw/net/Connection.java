package neprowaet.jpcw.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Connection {

    private Selector selector;
    public boolean work = true;

    public InetSocketAddress address;

    public Client newClient(String username, String password) {
        try {

            Client client = new Client(this);

            SocketChannel socket = SocketChannel.open();
            socket.configureBlocking(false);
            SelectionKey selectionKey = socket.register(selector, SelectionKey.OP_CONNECT);
            selectionKey.attach(client);

            client.channel = socket;
            client.data.ConnectionData.username = username;
            client.data.ConnectionData.password = password;
            return client;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Connection(String hostname, int port) {
        this.address = new InetSocketAddress(hostname, port);
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread worker = new Thread(() -> handleSelectedKeys());
        worker.start();
    }

    private void handleSelectedKeys() {
        while (work) {
            int selectableChannels = 0;
            try {
                while (work && selectableChannels == 0) {
                    selectableChannels = selector.selectNow();
                    Thread.onSpinWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (work && it.hasNext()) {
                SelectionKey key = it.next();

                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key.channel();

                    Client connectedClient = (Client) key.attachment();
                    try {
                        if (channel.finishConnect()) {
                            SelectionKey clientReadKey = key.channel().register(selector, SelectionKey.OP_READ);
                            clientReadKey.attach(connectedClient);
                        } else {
                            System.out.println("can't finish connection");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    try {
                        Client client = (Client) key.attachment();
                        client.read(channel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                it.remove();
            }
        }
    }
}
