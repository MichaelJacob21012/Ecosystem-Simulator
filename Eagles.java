import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a eagles.
 * Eagles age, move, breed, eat rabbits, catch and spread diseases and die.
 * 
 * @author Daniel Ratiu and Michael Jacob
 * @version 22/02/2018
 */
public class Eagles extends Animal
{
    // Characteristics shared by all Eagles (class variables).

    // The age at which an eagle can start to breed.
    private static final int BREEDING_AGE = 12;
    // The age to which an eagle can live.
    private static final int MAX_AGE = 250;
    // The likelihood of an eagle breeding.
    private static final double BREEDING_PROBABILITY = 0.18;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single rabbit. In effect, this is the
    // number of steps an eagle can go before it has to eat again.
    private static final int RABBIT_FOOD_VALUE = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The chance of an infected eagle dying of disease at each step.
    private static final double DISEASE_DEATH_PROBABILITY = 0.27;
    // Individual characteristics (instance fields).
    // The eagle's age.
    private int age;
    // The eagle's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create an eagle. An eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Eagles(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = RABBIT_FOOD_VALUE;
        }
    }

    /**
     * Increase the age. This could result in the eagle's death.
     */
    public void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this eagle more hungry. This could result in the eagle's death.
     */
    public void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    public Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    rabbit.setDead();
                    foodLevel = RABBIT_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this eagle is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newEagles A list to return newly born eagles.
     */
    public void giveBirth(List<Animal> newEagles)
    {
        // New eagles are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Eagles young = new Eagles(false, field,loc);
            newEagles.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
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
     * An eagle can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Attempt to spread the eagle's disease to adjacent eagles.
     */
    public void spreadDisease()
    {
        if(!getInfected()){
            return;     // There is no disease to spread.
        }
        
        if(checkDeath()){
            return;     // The eagle has died of disease.
        }
        //Spread disease to adjacent eagles
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Eagles) {
                Eagles eagle = (Eagles) animal;
                if(eagle.isAlive()) { 
                    eagle.infect();
                }
            }
        }
    }

    /**
     * Check to see if the eagle has died from disease.
     * @return True if the eagle died from disease.
     */
    public boolean checkDeath()
    {
        if( rand.nextDouble() <= DISEASE_DEATH_PROBABILITY){
            setDead();
            return true;
        }
        return false;
    }

    /**
     * @return The chance the eagle has of surviving in snow.
     */
    public double calculateSnowSurvivalProbability()
    {
        return 0.5 + (age*age*1.6)/(MAX_AGE*MAX_AGE);
    }
}
