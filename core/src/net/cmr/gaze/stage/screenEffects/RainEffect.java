package net.cmr.gaze.stage.screenEffects;

import java.awt.Point;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.Weather;
import net.cmr.gaze.world.Weather.WeatherType;
import net.cmr.gaze.world.entities.Player;

public class RainEffect extends ScreenEffect {

    final int MAX_RAINDROPS = 100;
    final float RAINDROP_SCALE = 1.5f;
    final float RAINDROP_LIFE = 1.9f;
    final float RAINDROP_DELTA = .05f;
    float effectDelta = 0;
    ConcurrentHashMap<Point, Float> raindrops = new ConcurrentHashMap<>();

    @Override
    public void render(GameScreen screen, FrameBuffer buffer) {
        Player player = screen.getLocalPlayer();
        boolean heavyRain = Weather.getWeather(screen.getEnvironmentController()) == WeatherType.THUNDER;
        if(player == null) {
            return;
        }
        Point playerCoordinates = player.getTileCoordinates();
        effectDelta+=Gdx.graphics.getDeltaTime();
        if(effectDelta > RAINDROP_DELTA / (heavyRain ? 3f : 1f) && raindrops.size() < MAX_RAINDROPS * (heavyRain ? 2f : 1)) {
            effectDelta = 0;
            Point p = new Point((int) (playerCoordinates.x + (((Math.random()-.5d)*2d) * (Chunk.CHUNK_SIZE))), (int) (playerCoordinates.y + (((Math.random()-.5d)*2d) * (Chunk.CHUNK_SIZE))));
            raindrops.put(p, 0f);
        }

        batch().begin();
        for(Point p : raindrops.keySet()) {
            float delta = raindrops.get(p);
            delta+=Gdx.graphics.getDeltaTime();
            if(delta > RAINDROP_LIFE) {
                raindrops.remove(p);
            } else {
                batch().draw(gaze().getAnimation("waterParticle").getKeyFrame(delta), p.x*Tile.TILE_SIZE, p.y*Tile.TILE_SIZE, Tile.TILE_SIZE*(5f/4f)*RAINDROP_SCALE, Tile.TILE_SIZE*2*RAINDROP_SCALE);
                raindrops.put(p, delta);
            }
        }
        batch().end();
    }
    
}
