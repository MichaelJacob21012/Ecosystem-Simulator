import java.util.List;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author Daniel Ratiu and Michael Jacob
 * @version 22/02/2018
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // Whether the animal is infected by disease.
    private boolean infected;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        infected = false;
        this.field = field;
        setLocation(location);
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    public void act(List<Animal> newAnimals){
        incrementAge();
        incrementHunger();
        if (isAlive()){
            spreadDisease(); // This could kill the animal.
        }

        if(isAlive()) {
            // Create new animals.
            giveBirth(newAnimals);            
            // Look for food.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }

    }

    /**
     * Default method for day actions is to do normal action but this
     * can be overriden for an animal
     * @param newAnimals A list to receive newly born animals.
     */
    public void dayAct (List<Animal> newAnimals){ 
        act(newAnimals);
    }

    /**
     * Default method for night actions is to do normal action but this
     * can be overriden for an animal
     * @param newAnimals A list to receive newly born animals.
     */
    public void nightAct (List<Animal> newAnimals){ 
        act(newAnimals);
    }

    /**
     * Increase the animal's age.
     */
    public abstract void incrementAge();

    /**
     * Increase the animal's hunger.
     */
    public abstract void incrementHunger();

    /**
     * Give birth to new animals.
     * @param newAnimals A list to receive newly born animals.
     */
    public abstract void giveBirth(List <Animal> newAnimals);

    /**
     * Search for food.
     * @return The location of the food, null if not found.
     */
    public abstract Location findFood();

    /**
     * Calculate the chance of surviving a step in snow.
     * @return The chance of surviving a step in snow.
     */
    public abstract double calculateSnowSurvivalProbability();

    public boolean getInfected()
    {
        return infected;
    }

    public void infect()
    {
        infected = true;
    }

    /**
     * Spread disease to nearby animals.
     */
    public abstract void spreadDisease();        

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        infected = false;
        //Remove from location
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }

}
