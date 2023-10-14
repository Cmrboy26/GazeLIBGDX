package net.cmr.gaze.research;

import java.io.InvalidObjectException;
import java.util.HashMap;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class ResearchTree {  

        public ResearchVertex root;
        public final String namespace;
        public HashMap<String, ResearchVertex> researchNodes;

        public ResearchTree(String namespace) {
            this.namespace = namespace;
            researchNodes = new HashMap<>();
        }

        public HashMap<String, ResearchVertex> getResearchNodes() {
            return researchNodes;
        }

        public static ResearchTree deriveResearchGraph(String inputString) throws InvalidObjectException {

            JsonReader reader = new JsonReader();
            JsonValue mainJSONObject = reader.parse(inputString);

            ResearchTree graph = new ResearchTree(mainJSONObject.getString("namespace"));
            graph.root = new ResearchVertex(graph, mainJSONObject.get("root"));
            graph.researchNodes.put(graph.root.getID(), graph.root);

            for(JsonValue value : mainJSONObject.get("researchNodes")) {
                ResearchVertex vertex = graph.createVertex(value);
            }

            return graph;
        }

        private ResearchVertex createVertex(JsonValue value) {
            ResearchVertex vertex = new ResearchVertex(this, value);
            researchNodes.put(vertex.getID(), vertex);
            return vertex;
        }

        public ResearchVertex getVertex(String id) {
            if(id.equals("root"))
                return root;
            return researchNodes.get(id);
        }

        public void addChild(ResearchVertex parent, ResearchVertex child) {
            parent.children.add(child);
            child.parent = parent;
        }

        public String getUniversalID(ResearchVertex vertex) {
            if(vertex.equals(root)) {
                return namespace + ":" + root.getID();
            }
            return namespace + ":" + root.getID() + "." + vertex.getID();
        }

    }
