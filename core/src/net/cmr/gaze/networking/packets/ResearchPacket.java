package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.research.ResearchData;

@PacketID(id = 27)
public class ResearchPacket extends Packet {

    private String universalID;
    private boolean researched;
    private ResearchData data;
    
    public ResearchPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public ResearchPacket(String universalID, boolean researched) {
        this.universalID = universalID;
        this.researched = researched;
        this.data = null;
	}
    public ResearchPacket(String universalID) {
        this.universalID = universalID;
        this.researched = false;
        this.data = null;
    }
    public ResearchPacket(ResearchData data) {
        this.universalID = null;
        this.researched = false;
        this.data = data;
    }

    @Override
    protected void writePacketData(DataBuffer buffer) throws IOException {
        if(data==null) {
            buffer.writeInt(0);
            buffer.writeUTF(universalID);
            buffer.writeBoolean(researched);
        } else {
            buffer.writeInt(1);
            ResearchData.write(data, buffer);
        }
    }

    @Override
    public void readPacketData(DataInputStream input, int packetSize) throws IOException {
        int type = input.readInt();
        if(type==0) {
            universalID = input.readUTF();
            researched = input.readBoolean();
            data = null;
        } else {
            universalID = null;
            researched = false;
            data = ResearchData.read(input);
        }
    }
    
    public String getUniversalID() {
        return universalID;
    }
    
    public boolean isResearched() {
        return researched;
    }
    
    public ResearchData getData() {
        return data;
    }

}
