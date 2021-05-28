package neprowaet.jpcw.net;

import neprowaet.jpcw.io.BinaryPacketStream;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.PacketRegistry;
import neprowaet.jpcw.io.PacketSerializator;
import neprowaet.jpcw.net.packet.server.Challenge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class Connection {

    private Selector selector;
    public boolean work = true;


    protected InetSocketAddress address;

    public Client newClient(String username, String password) {
        try {

            Client client = new Client(this);

            SocketChannel socket = SocketChannel.open();
            socket.configureBlocking(false);
            SelectionKey selectionKey = socket.register(selector, SelectionKey.OP_CONNECT);
            selectionKey.attach(client);
            socket.connect(address);


            client.data.AuthorizationData.username = username;
            client.data.AuthorizationData.password = password;
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

    public Connection() throws Exception {

        selector = Selector.open();

        SocketChannel t1 = SocketChannel.open();
        t1.configureBlocking(false);
        t1.register(selector, SelectionKey.OP_CONNECT);

        SocketChannel t2 = SocketChannel.open();
        t2.configureBlocking(false);
        t2.register(selector, SelectionKey.OP_CONNECT);

        Thread worker = new Thread(() -> handleSelectedKeys());
        worker.start();

        PacketListener<Challenge> listener = c -> printByteArr(c.version);


        t1.connect(new InetSocketAddress("link.comeback.pw", 29001));
        t2.connect(new InetSocketAddress("link.comeback.pw", 29001));


        Thread.sleep(5000);
        work = false;
        t1.close();
    }

    public static void main(String[] args) throws Exception {

        Connection c = new Connection();


    }

    private void handleSelectedKeys() {
        while (work) {
            //System.out.println("phase1");
            int selectableChannels = 0;
            try {
                while (work && selectableChannels == 0) {
                    selectableChannels = selector.selectNow();
                    Thread.onSpinWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            //System.out.println("phase2");

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (work && it.hasNext()) {
                SelectionKey key = it.next();

                if (key.isConnectable()) {
                    // a connection was established with a remote server.
                    //System.out.println("hai. tryna connect");
                    SocketChannel channel = (SocketChannel) key.channel();

                    Client connectedClient = (Client) key.attachment();
                    try {
                        if (channel.finishConnect()) {
                            //System.out.println("succ");
                            SelectionKey clientReadKey = key.channel().register(selector, SelectionKey.OP_READ);
                            clientReadKey.attach(connectedClient);
                        } else {
                            System.out.println("fail");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (key.isReadable()) {
                    System.out.println("ridin");
                    ByteBuffer bb = ByteBuffer.allocate(1024);
                    int readedLenght = 0;
                    SocketChannel channel = (SocketChannel) key.channel();
                    try {


                        Client client = (Client) key.attachment();

                        client.read(channel);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (key.isWritable()) {
                    // a channel is ready for writing
                }

                it.remove();
            }

        }
    }

    public static void printByteArr(byte[] d) {
        for (byte b : d) {
            Integer ii = b & 0xff;
            System.out.printf("%02x ", (b & 0xff));
        }        //        ByteBuffer bb = ByteBuffer.allocate(1024 * 1);
//        t1.read(bb);
//        UnsignedBuffer b = new UnsignedBuffer(ar);
//        System.out.println(b.readCUint());
//        System.out.println(b.readCUint());
//        printByteArr(bb.array());
        System.out.println();

    }
}
