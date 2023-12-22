## GazeLIBGDX

This project is for a 2D LIBGDX game about exploring the universe, exploiting the "infinite" galaxy's resources, building intricate and functional bases, and so much more.

## Programming

### Adding a New Item

Short and sweet: create a new `ItemType` enum (located inside Items.java), pass your subclass object that extends Item into the enum, and implement the neccesary methods into the subclass object based on documentation.

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

WARNING (UNLIKELY OCCURANCE, skip unless you're experiencing buggy behavior): The name of the enum will be hashed into an integer and used for hashmaps and saving/loading/transmitting/recieving items through the `int getID()` method, and collisions ARE (theoretically) possible! An error will be transmitted in console if the game is run and there is a name collision. There is a remedy for this error in place (the ID is incremented untill the conflict is resolved), but attempt to resolve this issue by altering the name of the item if possible.


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
