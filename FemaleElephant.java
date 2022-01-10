import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A female elephant. It can give birth.
 * 
 * @author Daniel Ratiu and Michael Jacob
 * @version 22/02/2018
 */
public class FemaleElephant extends Elephant
{

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new female elephant. A elephant may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the elephant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public FemaleElephant(boolean randomAge, Field field, Location location)
    {
        super(randomAge,field, location);
    }

    /**
     * Check whether or not this elephant is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newelephant A list to return newly born elephant.
     */
    public void giveBirth(List<Animal> newelephant)
    {
        // New elephant are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();

        if( free.size() > 4){               // Prevent overcrowding
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);        
                int genderDecider = rand.nextInt(2);
                if (genderDecider == 0){
                    FemaleElephant young = new FemaleElephant (false, field, loc);
                    newelephant.add(young);
                }
                else{
                    MaleElephant young = new MaleElephant(false, field, loc);
                    newelephant.add(young);
                }
            }
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
        if(canBreed() && rand.nextDouble() <= Elephant.BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A elephant can breed if it has reached the breeding age.
     * @return true if the elephant can breed, false otherwise.
     */
    private boolean canBreed()
    {
        if(findBreedingPartner()){
            return age >= BREEDING_AGE;
        }
        return false;
    }

    /**
     * Look for a mature male elephant adjacent to the current location.
     * Necessary for breeding.
     * @return True if a male was found.
     */
    private boolean findBreedingPartner()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Elephant) {
                Elephant elephant = (Elephant) animal;
                if(elephant.isAlive()) { 
                    if(elephant instanceof MaleElephant){
                        MaleElephant male = (MaleElephant) elephant;
                        if(male.canBreed()){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
