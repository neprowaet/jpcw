package neprowaet.jpcw.net.packet.server;

import neprowaet.jpcw.data.AuthorizationData;
import neprowaet.jpcw.data.Handler;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.net.packet.types.ServerPacket;

public class OnlineAnnounce extends Packet implements Handler<AuthorizationData>, ServerPacket {

    public long userid;
    public long localsid;
    public long remain_time;
    public byte zoneid;
    public long free_time_left;
    public long free_time_end;
    public long creatime;
    public byte passwd_flag;
    public byte usbbind;
    public byte accountinfo_flag;

    @Override
    public void handleData(AuthorizationData dataBlock) {

    }
}
    /*
    	<protocol debug="0" name="OnlineAnnounce" maxsize="64" prior="101" type="4">
		<variable name="userid" type="int" />
		<variable name="localsid" type="unsigned int" />
		<variable name="remain_time" type="int" default="0"/>
		<variable name="zoneid" type="char" default="0"/>
		<variable name="free_time_left" type="int" default="0"/>
		<variable name="free_time_end" type="int" default="0"/>
		<variable name="creatime" type="int" default="0"/>
		<variable name="referrer_flag" type="char" default="0"/> <!--0 auÒÑ¾­Ìá¹©ÍÆ¹ãid 1 auÃ»ÓÐÌá¹©ÍÆ¹ãid-->
		<variable name="passwd_flag" type="char" default="0"/>	<!--bit0:ÃÜÂëÌ«¾ÃÎ´¸ü»» bit1:ÃÜ±£¿¨Ì«¾ÃÎ´¸ü»»-->
		<variable name="usbbind" type="char" default="0"/>
		<variable name="accountinfo_flag" type="char" default="0"/>	 <!--bit0:ÐèÒª²¹ÌîÉí·ÝÖ¤ºÍÐÕÃû bit1:ÐèÒª²¹ÌîÕËºÅÃÜÂëÓÊÏä-->
	</protocol>
     */