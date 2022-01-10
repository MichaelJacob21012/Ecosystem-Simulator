import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a Cow.
 * Cows age, move, breed, eat plants, catch and spread diseases, and die.
 * 
 * @author Daniel Ratiu and Michael Jacob
 * @version 22/02/2018
 */
public class Cow extends Animal
{
    // Characteristics shared by all cows (class variables).

    // The age at which a cow can start to breed.
    private static final int BREEDING_AGE = 8;
    // The age to which a cow can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a cow breeding.
    private static final double BREEDING_PROBABILITY = 0.16;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The size of plants a cow can eat per step.
    private static final int FOOD_CAPACITY = 5;
    // The food value of a single plant. In effect, this is the
    // number of steps a cow can go before it has to eat again.
    private static final int PLANT_FOOD_VALUE = 7;
    // The chance of an infected cow dying of disease at each step.    
    private static final double DISEASE_DEATH_PROBABILITY = 0.37;
    // Individual characteristics (instance fields).
    
    // The cow's food level, which is increased by eating plants.
    private int foodLevel;
    // The cow's age.
    private int age;

    /**
     * Create a new cow. A cow may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the cow will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cow(boolean randomAge, Field field, Location location)
    {
        super(field, location);
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
     * This could result in the cow's death.
     */
    public void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this cow more hungry. This could result in the cow's death.
     */
    public void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * The behaviour of a cow during the night. 
     * The cow is asleep so does not find food, move or breed.
     * @param newAnimals A list to receive newly born animals - will remain empty.
     */
    public void nightAct(List<Animal> newAnimals){
        incrementAge();
        incrementHunger();
        if (isAlive()){
            if(getInfected()){
                spreadDisease();
            }
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
     * Check whether or not this cow is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCows A list to return newly born cows.
     */
    public void giveBirth(List<Animal> newCows)
    {
        // New cows are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Cow young = new Cow(false, field, loc);
            newCows.add(young);
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
     * A cow can breed if it has reached the breeding age.
     * @return true if the cow can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Attempt to spread the cow's disease to adjacent cows.
     */
    public void spreadDisease()
    {
        if(!getInfected()){
            return;     // There is no disease to spread.
        }
        
        if(checkDeath()){
            return;     // The cow has died of disease.
        }
        //Spread disease to ajacent cows
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Cow) {
                Cow cow = (Cow) animal;
                if(cow.isAlive()) { 
                    cow.infect();
                }
            }
        }
    }

    /**
     * Check to see if the cow has died from disease.
     * @return True if the cow died from disease.
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
     * @return The chance the cow has of surviving in snow.
     */
    public double calculateSnowSurvivalProbability()
    {
        return 0.5 + (age*age*1.6)/(MAX_AGE*MAX_AGE);
    }
}
