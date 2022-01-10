import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, eat plants, catch and spread diseases, and die.
 * 
 * @author Daniel Ratiu and Michael Jacob
 * @version 22/02/2018
 */
public class Rabbit extends Animal
{
    // Characteristics shared by all rabbits (class variables).

    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a rabbit can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a rabbit breeding.
    private static final double BREEDING_PROBABILITY = 0.37;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The size of plants a rabbit can eat per step.
    private static final int FOOD_CAPACITY = 1;
    // The food value of a single plant. In effect, this is the
    // number of steps a rabbit can go before it has to eat again.
    private static final int PLANT_FOOD_VALUE = 8;
    // The chance of an infected rabbit dying of disease at each step.    
    private static final double DISEASE_DEATH_PROBABILITY = 0.37;
    // Individual characteristics (instance fields).

    // The rabbit's age.
    private int age;
    // The rabbit's food level, which is increased by eating plants.
    private int foodLevel;
    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age and food level.
     * 
     * @param randomAge If true, the rabbit will have a random age and food level..
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rabbit(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANT_FOOD_VALUE);
        }else {
            age = 0;
            foodLevel = PLANT_FOOD_VALUE;
        }
    }
    
    /**
     * Increase the age.
     * This could result in the rabbit's death.
     */
    public void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this rabbit more hungry. This could result in the rabbit's death.
     */
    public void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for plants adjacent to the current location.
     * Only the first live plant is eaten.
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
            if(animal instanceof Plant) {
                Plant plant = (Plant) animal;
                if(plant.isAlive()) { 
                    plant.reduceSize(FOOD_CAPACITY);
                    foodLevel = PLANT_FOOD_VALUE;
                    if(plant.isAlive()){
                        return null;
                    }
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRabbits A list to return newly born rabbits.
     */
    public void giveBirth(List<Animal> newRabbits)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Rabbit young = new Rabbit(false, field, loc);
            newRabbits.add(young);
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
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * Attempt to spread the rabbit's disease to adjacent rabbits if it is
     * infected.
     */
    public void spreadDisease()
    {
        if(!getInfected()){
            return;     // There is no disease to spread
        }
        
        if(checkDeath()){
            return;     // The rabbit has died of disease.
        }
        //If it is still alive it can soread disease to other rabbits in
        //adjacent locations
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    rabbit.infect(); 
                }
            }
        }
    }
    
    /**
     * Check to see if the rabbit has died from disease.
     * @return True if the rabbit died from disease.
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
     * @return The chance the rabbit has of surviving in snow.
     */
    
    public double calculateSnowSurvivalProbability()
    {
        return 0.9;
    }
}
