package neprowaet.jpcw;

import neprowaet.jpcw.data.gametypes.PShopItemEntry;
import neprowaet.jpcw.net.Client;
import neprowaet.jpcw.net.Connection;
import neprowaet.jpcw.net.FuturePacket;
import neprowaet.jpcw.net.packet.client.PShopListItem;
import neprowaet.jpcw.net.packet.server.PShopListItem_Re;
import neprowaet.jpcw.net.packet.server.WorldChat;
import neprowaet.jpcw.net.security.MPPC;

public class Example {
    public static void main(String[] args) throws InterruptedException {
        Connection connection = new Connection("localhost", 29000);
        Client client = connection.newClient("username", "password");
        client.enableAutoAuth();
        client.addDecoder(new MPPC());
        client.connect();

        client.addListener(WorldChat.class, p -> System.out.println(p.name + ": " + p.msg));

        PShopListItem request = new PShopListItem(32913, 99);
        FuturePacket<PShopListItem_Re> future = client.sendAndWaitFor(request, PShopListItem_Re.class);
        PShopListItem_Re response = future.get();

        for (PShopItemEntry p : response.entries)
            System.out.println("id: " + p.pShopItem.item.id + " price: " + p.pShopItem.price);

        client.disconnect();
    }
}
