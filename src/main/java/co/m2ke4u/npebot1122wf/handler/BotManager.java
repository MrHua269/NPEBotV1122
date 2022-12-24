package co.m2ke4u.npebot1122wf.handler;

import co.m2ke4u.npebot1122wf.forgesupport.MCForge;
import co.m2ke4u.npebot1122wf.forgesupport.MCForgeInject;
import co.m2ke4u.npebot1122wf.forgesupport.MCForgeMOTD;
import org.spacehq.mc.auth.exception.request.RequestException;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.packetlib.*;
import org.spacehq.packetlib.tcp.TcpClientSession;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

public class BotManager {
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
                Map<String,String> modList = new MCForgeMOTD().pingGetModsList(client.getHost(),client.getPort(),340);
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

    public static SessionFactory getNewSessionForgedFactoryWithProxy(Proxy proxy){
        return new SessionFactory() {
            @Override
            public Session createClientSession(Client client) {
                TcpClientSession wrapped = new TcpClientSession(client.getHost(),client.getPort(),client.getPacketProtocol(),client,proxy);
                wrapped.setConnectTimeout(30000);
                wrapped.setReadTimeout(3000);
                wrapped.setWriteTimeout(3000);
                Map<String,String> modList = new MCForgeMOTD().pingGetModsList(client.getHost(),client.getPort(),340);
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

    public static SessionFactory getNewSessionFactoryWithProxy(Proxy proxy){
        return new SessionFactory() {
            @Override
            public Session createClientSession(Client client) {
                TcpClientSession wrapped = new TcpClientSession(client.getHost(),client.getPort(),client.getPacketProtocol(),client,proxy);
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

    public static Client wrapNewClient(InetSocketAddress targetAddress, String onlineId, String accessToken, boolean enableForge, Proxy proxy){
        try {
            return new Client(targetAddress.getHostName(), targetAddress.getPort(),new MinecraftProtocol(onlineId,accessToken,true),enableForge ? getNewSessionForgedFactoryWithProxy(proxy) : getNewSessionFactoryWithProxy(proxy));
        } catch (RequestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Client wrapNewClient(InetSocketAddress targetAddress,String offlineId,boolean enableForge,Proxy proxy){
        return new Client(targetAddress.getHostName(), targetAddress.getPort(),new MinecraftProtocol(offlineId),enableForge ? getNewSessionForgedFactoryWithProxy(proxy) : getNewSessionFactoryWithProxy(proxy));
    }
}
