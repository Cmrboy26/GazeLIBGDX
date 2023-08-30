package net.cmr.gaze.networking;

import java.awt.Point;
import java.util.HashMap;

import com.badlogic.gdx.utils.Predicate;

import net.cmr.gaze.util.Vector2Double;
import net.cmr.gaze.world.entities.Player;

public class ConnectionPredicates {
	GameServer server;
	//public final Predicate<String> PLAYER_IN_BOUNDS;
	//public final Predicate<String> SEND_ALL;
	
	HashMap<ConnectionPredicate, Predicate<Object[]>> predicateMap;
	
	public ConnectionPredicates(GameServer server) {
		this.server = server;
		predicateMap = new HashMap<>();
		
		predicateMap.put(ConnectionPredicate.PLAYER_IN_BOUNDS, new Predicate<Object[]>() {
			// [0]: Chunk coordinates
			// [1]: World 
			@Override
			public boolean evaluate(Object[] object) {
				Object[] params = (Object[]) object[1];
				
				Point coordinates = (Point) params[0];
				if(server.connections.get(object[0]) == null) {
					return false;
				}
				Player player = server.connections.get(object[0]).getPlayer();
				if(player == null) {
					return false;
				}
				if(!params[1].equals(player.getWorld())) {
					return false;
				}
				
				
				boolean output = new Vector2Double(player.getChunk()).chebyshev(new Vector2Double(coordinates)).intValue()<=1;
				return output;
			}
		});
		predicateMap.put(ConnectionPredicate.PLAYER_NOW_IN_BOUNDS, new Predicate<Object[]>() {
			// [0]: Chunk coordinates
			// [1]: Last chunk coordinates
			// [2]: World
			// [3]: (optional) forceSend (use if the object was just added while in range)
			@Override
			public boolean evaluate(Object[] object) {
				Object[] params = (Object[]) object[1];
				
				Point coordinates = (Point) params[0], lastCoordinates = (Point) params[1];
				if(server.connections.get(object[0]) == null) {
					return false;
				}
				Player player = server.connections.get(object[0]).getPlayer();
				if(player == null) {
					return false;
				}
				if(!player.getWorld().equals(params[2])) {
					return false;
				}
				
				boolean inLoading = new Vector2Double(player.getChunk()).chebyshev(new Vector2Double(coordinates)).intValue()<=1;
				boolean previouslyInLoading = new Vector2Double(player.getLastChunk()).chebyshev(new Vector2Double(lastCoordinates)).intValue()<=1;
				
				if(params.length==4) {
					previouslyInLoading = (boolean) params[3];
				}
				
				
				return inLoading && !previouslyInLoading;
			}
		});
		predicateMap.put(ConnectionPredicate.SEND_ALL, new Predicate<Object[]>() {
			@Override
			public boolean evaluate(Object[] object) {
				return true;
			}
		});
	}
	
	public boolean evaluate(ConnectionPredicate predicate, Object[] objects) {
		// [0]: username (ALWAYS), [1]: Object[] paramaters
		return predicateMap.get(predicate).evaluate(objects);
	}
	
	public enum ConnectionPredicate {
		
		PLAYER_NOW_IN_BOUNDS,
		PLAYER_IN_BOUNDS,
		SEND_ALL
		
	}
	
	
}
