package net.cmr.gaze.stage.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.cmr.gaze.Gaze;

public class TechMenu extends WidgetGroup {
    
    Gaze game;

    ButtonGroup<ImageButton> categoryButtonGroup;
    ImageButton confirmButton;

    public TechMenu(Gaze game) {
        this.game = game;
        setBounds(0, 0, 640, 360);

        Image image = new Image(game.getSprite("techBackground"));
        image.setBounds(57*2, 36*2, 206*2, 108*2);
        addActor(image);

        categoryButtonGroup = new ButtonGroup<>();
        categoryButtonGroup.setMinCheckCount(1);
        categoryButtonGroup.setMaxCheckCount(1);

        for(int i = 0; i < 7; i++) {
            TextureRegionDrawable icon = new TextureRegionDrawable(game.getSprite("techCategoryIcon"+(i+1)));
            icon.setMinSize(11*2, 11*2);
            ImageButton button = new ImageButton(icon);
            final int touchIndex = i;
            button.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println(touchIndex);
                    return true;
                }
            });
            button.setBounds(57*2+6*2, 36*2+108*2-17*2-(i*14*2), 11*2, 11*2);
            categoryButtonGroup.add(button);
            addActor(button);
        }

        TextureRegionDrawable researchButton = new TextureRegionDrawable(game.getSprite("researchConfirm"));
        researchButton.setMinSize(16, 16);
        confirmButton = new ImageButton(researchButton);
        confirmButton.setBounds(57*2+184*2, 36*2+8*2, 8*2, 8*2);
        confirmButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("CONFIRM TECH");
                   return true;
            }
        });
        addActor(confirmButton);

        ScrollPaneStyle style = new ScrollPaneStyle();
        ScrollPane researchPanel = new ScrollPane(null, style);
        researchPanel.setBounds(57*2+24*2, 36*2+21*2, 168*2, 81*2);

        addActor(researchPanel);
    } 

    // TODO: Create a directed graph of research nodes that can be easily written in a text file using json
    // TODO: Create a parser that can read the text file and create the graph

    
    public static class ResearchGraph {

        private boolean[][] adjacencyGraph; // the first index is the "origin", and the second index is the vertex that the origin is directed towards
        private Map<Integer, ResearchVertex> vertexMap = new HashMap<>(); // the int is the index of the vertex in the adjacency graph

        private ResearchGraph() {
            
        }

        public static ResearchGraph deriveResearchTree(List<String> lines) {
            // parse the lines into a tree using json
            ResearchGraph graph = new ResearchGraph();
            graph.adjacencyGraph = new boolean[lines.size()][lines.size()];


            return graph;
        }

        public boolean pointsAt(ResearchVertex origin, ResearchVertex vertex) {
            return pointsAt(vertexMap.get(origin), vertexMap.get(vertex));
        }
        public boolean pointsAt(int origin, int vertex) {
            return adjacencyGraph[origin][vertex];
        }


    }
    
    private static class ResearchVertex {
        
        String json;

        // JSON needs to contain: requirements for research, research name, research description, research icon, PARENTS of the graph, and the position of the vertex in the graph
        private ResearchVertex(String json) {
            this.json = json;
        }

        public String getJSON() {
            return json;
        }
        public static ResearchVertex createVertex(String line) {
            return new ResearchVertex(line);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ResearchVertex) {
                return ((ResearchVertex) obj).getJSON().equals(getJSON());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return getJSON().hashCode();
        }

    }
}
