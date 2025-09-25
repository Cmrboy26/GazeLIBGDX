## GazeLIBGDX

Gaze is a libGDX game about exploring the universe, exploiting the galaxy's resources, and building intricate and functional bases.

## About Gaze

Gaze is a procedurally-generated 2D world where players and friends must exploit the resources around them to build a base, survive, and expand. Players must level up their skills, research technologies, and explore the underground to obtain the resources to design a functional base. Online multiplayer, powered by Java Sockets, allows you to connect to your friends anywhere and work and explore together.

Personally, this idea for a game had been in my head for a long while. I wanted a game that suited all of my friends' interests, which meant combining space, factory building, and casual elements in one package. However, the nearly unlimited scope of this project resulted in its abandonment, as it was too ambitious for me. This lesson about scope creep inspired me to organize myself and finally complete a game, which is what my other project, Project Tetra TD, became.

### Features
- Navigate an infinite, procedurally-generated world
- Harvest, craft, and place objects and workstations
- Level up skills to passively improve stats
- Research new technologies, unlocking new recipes and functional structures
- Grow and water crops
- Create a simple factory powered by electricity
- Save and load worlds
- Host a server to play with friends
- Custom music, audio, and graphics

### Technologies Used
- **Programming Language:** Java
- **Frameworks:** libGDX, Java Sockets
- **Tools:** Gradle, Git, Aseprite, FL Studio, Audacity
- **Platforms:** Windows

## Downloading, Playing, and Setup

To play the game, look at the "Releases" tab on the right and follow the instructions in the release description.

To run from source code:
1. Download the project.
2. In the project directory, ensure that you have Gradle installed run `./gradlew wrapper`.
3. Run `./gradlew desktop:run` to run the desktop program.

## Programming

### Adding a New Item

Short and sweet: create a new `ItemType` enum (located inside Items.java), pass your subclass object that extends Item into the enum, and implement the necessary methods into the subclass object based on documentation.

NOTE: This system for adding items was created without modding support in mind. In the future, changes MAY be made to support mods (depending on how much attention the game draws).

Before creating an item, you must go into the Items.java file and into the `ItemType` enum. Here are the constructors for the `ItemType` enum:
```java
ItemType(Class<? extends Item> clazz, int maxSize) {
    ...
}
ItemType(Class<? extends Item> clazz) {
    ...
}
```
The `clazz` variable is where you will specify the class object of your new item. The `maxSize` variable allows you to specify a stack size outside of the default 64 items per stack. For machinery or placables, this is typically set to 4.

Name the enum based on the desired English name of the item formatted in screaming snake case, which looks like this: `"BASIC_CUBE", "BREAD", "BASIC_CIRCUIT_BOARD"`. (The name of the enum will not influence the displayed name of the item in-game). In addition, be sure to EXCLUDE the word "ITEM" in your enum name, as this is not considered proper formating.

Here is an example of creating an `ItemType` for a generic cube item:

```java
public enum ItemType {
    //...
    GENERIC_CUBE(GenericCubeItem.java, 4),
    //...
}
```

WARNING (UNLIKELY OCCURANCE, skip unless you're experiencing buggy behavior): The name of the enum will be hashed into an integer and used for hashmaps and saving/loading/transmitting/receiving items through the `int getID()` method, and collisions ARE (theoretically) possible! An error will be transmitted to the console if the game is run and there is a name collision. There is a remedy for this error in place (the ID is incremented until the conflict is resolved), but attempt to resolve this issue by altering the name of the item if possible.


Now that the item type is created, you will need to create a new class for the item and have it extend one of the various `abstract Item` classes:
- `Item` (lowest level, for complete control of items)
- `BasicItem` (higher level, for non-functional items/materials)
- `Placeable` (for items that place a tile in the world)
- `FoodItem` (for items that are meant to be consumed)
- `SeedItem` (for seed items that are meant to plant a seed)

Here is an example of an item extending `BasicItem`:

```java
import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class GenericCubeItem extends BasicItem {

    public GenericCubeItem(int size) {
        super(ItemType.GENERIC_CUBE, size, "genericCubeSprite");
    }

    @Override
    public Item getItem(int size) {
        return new GenericCubeItem(size);
    }
    
}
```

If there are issues with rendering, it may be in your best interest to extend the lower level `Item` class and set the dimensions for rendering manually. Consider looking into pre-existing tile rendering methods such as the one in `SteamEngineItem`, `StoneBrickFloorItem`, and `TechnologyTableItem` for examples of this in action.

Note: There is implementation for reading and writing extra data in an item, but some of this data may not be respected at the current stage in development (as it has not been tested).
