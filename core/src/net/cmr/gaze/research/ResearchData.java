package net.cmr.gaze.research;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.stage.widgets.ResearchMenu;

public class ResearchData {
    private HashMap<String, Boolean> techs;

    public ResearchData() {
        techs = new HashMap<String, Boolean>();
        // set all roots of all trees to true
        for (ResearchTree tree : ResearchMenu.researchTrees) {
            techs.put(tree.getUniversalID(tree.root), true);
        }
    }

    public boolean isResearched(ResearchVertex vertex) {
        return isResearched(vertex.tree.getUniversalID(vertex));
    }

    public boolean isResearched(String techID) {
        Boolean researched = techs.get(techID);
        return researched != null && researched;
    }

    public void read(DataInputStream stream) throws IOException {
        int size = stream.readInt();
        for (int i = 0; i < size; i++) {
            String techID = stream.readUTF();
            boolean researched = stream.readBoolean();
            techs.put(techID, researched);
        }
    }

    public void write(DataBuffer buffer) throws IOException {
        buffer.writeInt(techs.size());
        for (String techID : techs.keySet()) {
            buffer.writeUTF(techID);
            buffer.writeBoolean(techs.get(techID));
        }
    }

    public void setResearched(ResearchVertex vertex, boolean researched) {
        techs.put(vertex.tree.getUniversalID(vertex), researched);
    }
}
