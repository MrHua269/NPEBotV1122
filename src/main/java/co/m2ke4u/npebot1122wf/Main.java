package co.m2ke4u.npebot1122wf;

import co.m2ke4u.npebot1122wf.handler.BotManager;
import co.m2ke4u.npebot1122wf.handler.ConfigManager;
import co.m2ke4u.npebot1122wf.handler.PacketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.packetlib.Client;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws InterruptedException {
        logger.info("NPEBOT v1.12.2 powered by M2ke4u");
        ConfigManager.init();
        Client c1 = getNewClient();
        c1.getSession().addListener(new PacketHandler(
                ConfigManager.getCurrentConfig().getUsername(),
                ConfigManager.getCurrentConfig().getAuthMePassword(),
                ConfigManager.getCurrentConfig().isEnableAuthmeMode(),
                ConfigManager.getCurrentConfig().isEnableAutoRespawn()
        ));
        while (true){
            c1.getSession().connect();
            Scanner scanner = new Scanner(System.in);
            while (c1.getSession().isConnected() && scanner.hasNext()){
                c1.getSession().send(new ClientChatPacket(scanner.nextLine()));
            }
            if (ConfigManager.getCurrentConfig().isAutoReconnect()) {
                Thread.sleep(5000);
            }else{
                System.exit(0);
            }
        }
    }

    public static Client getNewClient(){
        Client wrapped;
        if (ConfigManager.getCurrentConfig().isUseProxy()){
            if (ConfigManager.getCurrentConfig().isOnlineMode()){
                wrapped = BotManager.wrapNewClient(
                        new InetSocketAddress(
                                ConfigManager.getCurrentConfig().getServerAddress(),
                                ConfigManager.getCurrentConfig().getServerPort()),
                        ConfigManager.getCurrentConfig().getUsername(),
                        ConfigManager.getCurrentConfig().getAccessToken(),
                        true,
                        new Proxy(
                                Proxy.Type.HTTP,
                                new InetSocketAddress(
                                        ConfigManager.getCurrentConfig().getProxyAddress(),
                                        ConfigManager.getCurrentConfig().getProxyPort())
                        ));
            }else{
                wrapped = BotManager.wrapNewClient(
                        new InetSocketAddress(
                                ConfigManager.getCurrentConfig().getServerAddress(),
                                ConfigManager.getCurrentConfig().getServerPort()),
                        ConfigManager.getCurrentConfig().getUsername(),
                        true,
                        new Proxy(
                                Proxy.Type.HTTP,
                                new InetSocketAddress(
                                        ConfigManager.getCurrentConfig().getProxyAddress(),
                                        ConfigManager.getCurrentConfig().getProxyPort())
                        ));
            }
        }else{
            if (ConfigManager.getCurrentConfig().isOnlineMode()){
                wrapped = BotManager.wrapNewClient(
                        new InetSocketAddress(
                                ConfigManager.getCurrentConfig().getServerAddress(),
                                ConfigManager.getCurrentConfig().getServerPort()),
                        ConfigManager.getCurrentConfig().getUsername(),
                        ConfigManager.getCurrentConfig().getAccessToken(),
                        true);
            }else{
                wrapped = BotManager.wrapNewClient(
                        new InetSocketAddress(
                                ConfigManager.getCurrentConfig().getServerAddress(),
                                ConfigManager.getCurrentConfig().getServerPort()),
                        ConfigManager.getCurrentConfig().getUsername(),
                        true);
            }
        }
        return wrapped;
    }
}