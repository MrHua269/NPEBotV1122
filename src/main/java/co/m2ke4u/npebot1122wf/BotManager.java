package co.m2ke4u.npebot1122wf;

import co.m2ke4u.npebot1122wf.forgesupport.MCForge;
import co.m2ke4u.npebot1122wf.forgesupport.MCForgeInject;
import co.m2ke4u.npebot1122wf.forgesupport.MCForgeMOTD;
import org.spacehq.mc.auth.exception.request.RequestException;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.packetlib.*;
import org.spacehq.packetlib.tcp.TcpClientSession;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BotManager {
    private static final Map<InetSocketAddress,Map<String,String>> cachedModLists = new ConcurrentHashMap<>();
    private static final Map<String,Object> config = new HashMap<>();

    public static void initConfig(){

    }

    static {
        MCForgeInject.inject();
    }

    public static SessionFactory getNewSessionForgedFactory(){
        return new SessionFactory() {
            @Override
            public Session createClientSession(Client client) {
                TcpClientSession wrapped = new TcpClientSession(client.getHost(),client.getPort(),client.getPacketProtocol(),client,null);
                wrapped.setConnectTimeout(30000);
                wrapped.setReadTimeout(3000);
                wrapped.setWriteTimeout(3000);

                final InetSocketAddress serverAddress = new InetSocketAddress(client.getHost(),client.getPort());
                Map<String,String> modList = cachedModLists.containsKey(serverAddress) ? cachedModLists.get(serverAddress) : new MCForgeMOTD().pingGetModsList(client.getHost(),client.getPort(),340);

                if (!cachedModLists.containsValue(modList)){
                    cachedModLists.put(serverAddress,modList);
                }

                MCForge forge = new MCForge(wrapped,modList);
                forge.init();
                return wrapped;
            }

            @Override
            public ConnectionListener createServerListener(Server server) {
                return null;
            }
        };
    }

    public static SessionFactory getNewSessionFactory(){
        return new SessionFactory() {
            @Override
            public Session createClientSession(Client client) {
                TcpClientSession wrapped = new TcpClientSession(client.getHost(),client.getPort(),client.getPacketProtocol(),client,null);
                wrapped.setConnectTimeout(30000);
                wrapped.setReadTimeout(3000);
                wrapped.setWriteTimeout(3000);
                return wrapped;
            }

            @Override
            public ConnectionListener createServerListener(Server server) {
                return null;
            }
        };
    }

    public static Client wrapNewClient(InetSocketAddress targetAddress,String offlineId,boolean enableForge){
        return new Client(targetAddress.getHostName(), targetAddress.getPort(),new MinecraftProtocol(offlineId),enableForge ? getNewSessionForgedFactory() : getNewSessionFactory());
    }

    public static Client wrapNewClient(InetSocketAddress targetAddress,String onlineId,String accessToken,boolean enableForge){
        try {
            return new Client(targetAddress.getHostName(), targetAddress.getPort(),new MinecraftProtocol(onlineId,accessToken,true),enableForge ? getNewSessionForgedFactory() : getNewSessionFactory());
        } catch (RequestException e) {
            e.printStackTrace();
        }
        return null;
    }
}
