package net.cmr.gaze.world.interfaceTiles;

import java.util.Objects;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Housing;
import net.cmr.gaze.world.RenderRule;
import net.cmr.gaze.world.Tile;

public interface HousingWall extends WallTile, Housing {

    public abstract String getWallSpriteName();
    public default String getWallSprite(GameScreen screen) {
        if(Objects.equals(screen.currentRenderRule, RenderRule.HOUSE_RULE)) {
            return getWallSpriteName()+"Shortened";
        } else {
            return getWallSpriteName();
        }
    }

    public static void render(Gaze game, GameScreen screen, Tile wall, int x, int y) {
        if(!(wall instanceof HousingWall)) {
            return;
        }
        wall.draw(game.batch, game.getSprite(((HousingWall)wall).getWallSprite(screen)), x, y-1, 1, 3);
    }

}
