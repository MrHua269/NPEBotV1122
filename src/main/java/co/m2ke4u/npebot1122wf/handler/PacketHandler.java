package co.m2ke4u.npebot1122wf.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spacehq.mc.protocol.data.game.ClientRequest;
import org.spacehq.mc.protocol.data.game.entity.player.CombatState;
import org.spacehq.mc.protocol.data.message.Message;
import org.spacehq.mc.protocol.data.message.TextMessage;
import org.spacehq.mc.protocol.data.message.TranslationMessage;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientRequestPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerCombatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.event.session.*;
import org.spacehq.packetlib.packet.Packet;

public class PacketHandler implements SessionListener {
    private static final Logger logger = LogManager.getLogger();
    private Session currentSession;
    private final String currentUserName;
    private final String authMePassword;
    private final boolean enableAuthme;
    private final boolean enableAutoRespawn;
    private volatile boolean connected = false;

    public PacketHandler(String userName,String authmePassword,boolean enableAuthMeMode,boolean autoRespawn){
        this.currentUserName = userName;
        this.authMePassword = authmePassword;
        this.enableAuthme = enableAuthMeMode;
        this.enableAutoRespawn = autoRespawn;
    }

    @Override
    public void packetReceived(PacketReceivedEvent packetReceivedEvent) {
        final Packet packet = packetReceivedEvent.getPacket();
        if (packet instanceof ServerJoinGamePacket){
            this.connected = true;
            this.currentSession.send(new ClientRequestPacket(ClientRequest.RESPAWN));
            logger.info("[NPEBOT][SystemInfo]User {} connected to the server",this.currentUserName);
            this.processLoginPluginAsync();
        }

        if (packet instanceof ServerCombatPacket){
            ServerCombatPacket serverCombatPacket = ((ServerCombatPacket) packet);
            if (serverCombatPacket.getCombatState().equals(CombatState.ENTITY_DEAD)){
                logger.info("[NPEBOT][Misc]Bot {} dead",this.currentUserName);
                if (this.enableAutoRespawn){
                    logger.info("[NPEBOT][Misc]Auto respawning");
                    this.currentSession.send(new ClientRequestPacket(ClientRequest.RESPAWN));
                }
            }
        }

        if (packet instanceof ServerChatPacket){
            ServerChatPacket serverChatPacket = ((ServerChatPacket) packet);
            final Message message = serverChatPacket.getMessage();
            if (message instanceof TranslationMessage){
                TranslationMessage casted = ((TranslationMessage) message);
                logger.info("[NPEBOT][Chat]{}",casted.getFullText());
            }
            if (message instanceof TextMessage){
                TextMessage casted = ((TextMessage) message);
                logger.info("[NPEBOT][Chat]{}",casted.getFullText());
            }
        }
    }

    public void processLoginPluginAsync(){
        Thread processor = new Thread(()->{
            try {
                Thread.sleep(3000);
                if (this.enableAuthme){
                    this.currentSession.send(new ClientChatPacket("/reg " + this.authMePassword + " " + this.authMePassword));
                    logger.info("[NPEBOT][System]Auto authme register packet sent");
                    Thread.sleep(3000);
                    this.currentSession.send(new ClientChatPacket("/l " + this.authMePassword));
                    logger.info("[NPEBOT][System]Auto authme login packet sent");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        processor.setDaemon(true);
        processor.start();
    }

    @Override
    public void packetSent(PacketSentEvent packetSentEvent) {
        final Packet packet = packetSentEvent.getPacket();
        if (packet instanceof ClientKeepAlivePacket){
            logger.info("[NPEBOT][System]Keep alive packet sent,KeepAlvie id:{}",((ClientKeepAlivePacket) packet).getPingId());
        }
    }

    @Override
    public void connected(ConnectedEvent connectedEvent) {
        this.currentSession = connectedEvent.getSession();
    }

    @Override
    public void disconnecting(DisconnectingEvent disconnectingEvent) {}

    @Override
    public void disconnected(DisconnectedEvent disconnectedEvent) {
        logger.info("[NPEBOT][SystemInfo]Disconnected from server.Reason:{} Username:{} Cause:{}",disconnectedEvent.getReason(),this.currentUserName,disconnectedEvent.getCause());
        if (disconnectedEvent.getCause()!=null){
            disconnectedEvent.getCause().printStackTrace();
        }
    }
}
