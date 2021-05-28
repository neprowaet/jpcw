package neprowaet.jpcw;

import neprowaet.jpcw.net.Client;
import neprowaet.jpcw.net.Connection;
import neprowaet.jpcw.net.PacketListener;
import neprowaet.jpcw.net.packet.server.Challenge;
import neprowaet.jpcw.net.packet.server.OnlineAnnounce;

import java.util.Arrays;

public class Example {
    public static void main(String[] args) throws InterruptedException {
        Connection connection = new Connection("link.comeback.pw", 29001);

        Client c1 = connection.newClient("u", "p");


        c1.addListener(Challenge.class, c -> System.out.println(Arrays.toString(c.version)));

        System.out.println(c1.data.AuthorizationData.username);


        Thread.sleep(2000);
        connection.work = false;


    }
}
