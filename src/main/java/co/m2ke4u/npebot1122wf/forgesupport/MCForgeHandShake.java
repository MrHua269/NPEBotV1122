package co.m2ke4u.npebot1122wf.forgesupport;

import org.spacehq.packetlib.packet.Packet;

public abstract class MCForgeHandShake {
    protected MCForge forge;

    public MCForgeHandShake(MCForge forge) {
        this.forge = forge;
    }

    public abstract void handle(Packet recvPacket);
    public abstract String getFMLVersion();
}
