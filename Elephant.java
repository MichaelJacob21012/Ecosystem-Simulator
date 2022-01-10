import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a elephant.
 * Elephants age, move, breed, eat plants, catch and spread diseases, and die.
 * Elephants can be male or female.
 * 
 * @author Daniel Ratiu and Michael Jacob
 * @version 22/02/2018
 */
public abstract class Elephant extends Animal
{
    // Characteristics shared by all elephants (class variables).

    // The age at which a elephant can start to breed.
    protected static final int BREEDING_AGE = 5;
    // The age to which a elephant can live.
    protected static final int MAX_AGE = 200;
    // The likelihood of a elephant breeding.
    protected static final double BREEDING_PROBABILITY = 0.9;
    // The maximum number of births.
    protected static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    protected static final Random rand = Randomizer.getRandom();
    // The food value of a single plant. In effect, this is the
    // number of steps an elephant can go before it has to eat again.
    protected static final int PLANT_FOOD_VALUE = 10;
    // The size of plants an elephant can eat per step.
    protected static final int FOOD_CAPACITY = 8;
    // The chance of an infected elephant dying of disease at each step.
    protected static final double DISEASE_DEATH_PROBABILITY = 0.17;
    // Individual characteristics (instance fields).

    // The elephant's age.
    protected int age;
    // The elephant's food level, which is increased by eating plants.
    protected int foodLevel;
    /**
     * Create a new elephant. An elephant may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the elephant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Elephant(boolean randomAge, Field field, Location location)
    {
        super( field, location);
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
     * This could result in the elephant's death.
     */
    public void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this elephant more hungry. This could result in the elephant's death.
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
     * Attempt to spread the elephant's disease to adjacent elephants.
     */
    public void spreadDisease()
    {
        if(!getInfected()){
            return;     // There is no disease to spread.
        }

        if(checkDeath()){
            return;     // The elephant has died of disease.
        }
        //Infect adjacent elephants
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Elephant) {
                Elephant elephant = (Elephant) animal;
                if(elephant.isAlive()) { 
                    elephant.infect();
                }
            }
        }
    }

    /**
     * Check to see if the elephant has died from disease.
     * @return True if the elephant died from disease.
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
     * @return The chance the elephant has of surviving in snow.
     */
    public double calculateSnowSurvivalProbability()
    {
        return 0.5 + (age*age*1.6)/(MAX_AGE*MAX_AGE);
    }

    /**
     * Give birth to new elephants.
     * @param newAnimals A list to receive newly born animals
     */
    public abstract void giveBirth(List <Animal> newElephants);
}
