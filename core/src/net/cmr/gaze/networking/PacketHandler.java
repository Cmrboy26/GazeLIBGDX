package net.cmr.gaze.networking;

public abstract class PacketHandler {
    public PacketHandler() {
        
    } 
    public abstract void processPacket(Packet packet, PlayerConnection connection);
}
