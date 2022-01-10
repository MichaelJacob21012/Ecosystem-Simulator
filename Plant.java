import java.util.List;
import java.util.Random;

/**
 * A simple model of a plant.
 * Plants age, reproduce, grow and die.
 * 
 * @author Daniel Ratiu and Michael Jacob
 * @version 22/02/2018
 */
public class Plant
{
    // Characteristics shared by all plants (class variables).

    // The age at which a plant can start to reproduce.
    private static final int BREEDING_AGE = 2;
    // The age to which a plant can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a plant reproducing.
    private static final double BREEDING_PROBABILITY = 0.09;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The amount the plant increases in size per step.
    private static final int GROWTH_RATE = 3;
    // Individual characteristics (instance fields).
    
    // The plant's age.
    private int age;

    // Whether the plant is alive or not.
    private boolean alive;
    // The plant's field.
    private Field field;
    // The plant's position in the field.
    private Location location;
    // The size of the plant.
    private int size;
   
    /**
     * Create a new plant at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        size = 1;
    }
    
    /**
     * Check whether the plant is alive or not.
     * @return true if the plant is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    public void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
            size = 0;
        }
    
    }

    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
     */
    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the plant's field.
     * @return The plant's field.
     */
    public Field getField()
    {
        return field;
    }
    
    /**
     * The plant grows. Increases its size by its growth rate.
     */
    public void grow()
    {
        size += GROWTH_RATE;
    }
    
    /**
     * This is what the plant does most of the time - it reproduces 
     * if there is space, grows or sometimes dies of old age or overcrowding.
     * @param newPlants A list to return newly born plants.
     */
    public void act(List<Plant> newPlants)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newPlants);
            grow();
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation == null) {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the plant's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this plant is to create to new plants at this step.
     * New plants will be made into free adjacent locations.
     * @param newPlants A list to return newly grown plants.
     */
    private void giveBirth(List<Plant> newPlants)
    {
        // New plants are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant young = new Plant(field, loc);
            newPlants.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of new plants,
     * if it can reproduce.
     * @return The number of new plants (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A plant can reproduce if it has reached the required age.
     * @return true if the plant can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    public int getSize()
    {
        return size;
    }
    
    /**
     * Reduce the size of the plant by an amount. This may cause the plant to die.
     * @param The amount to reduce by.
     */
    public void reduceSize(int amountLost)
    {
        if (amountLost >= size){
            setDead();
            return;
        }
        size -= amountLost;
    }
    
    /**
     * Calculate the chance of surviving in wind. Larger plants are more likely to survive.
     * @return The chance of surviving one step in windy conditions.
     */
    public double calculateWindSurvivalProbability()
    {
        return 0.7 + size/20;
    }
    
    /**
     * Calculate the chance of surviving in snow. Larger plants are more likely to survive.
     * @return The chance of surviving one step in snowy conditions.
     */
    public double calculateSnowSurvivalProbability()
    {
        return 0.7 + size/50;
    }
}
