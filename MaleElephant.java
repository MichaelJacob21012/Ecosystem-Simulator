import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A male elephant. It can not give birth.
 * 
 * @author Daniel Ratiu and Michael Jacob
 * @version 22/02/2018
 */
public class MaleElephant extends Elephant
{
    /**
     * Create a new male elephant. A elephant may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the elephant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public MaleElephant(boolean randomAge, Field field, Location location)
    {
        super(randomAge,field, location);
    }

    /**
     * The male elephant doesn't give birth.
     */
    public void giveBirth(List<Animal> newelephant)
    {
        return;
    }

    /**
     * A male elephant can breed if it has reached the breeding age.
     * @return true if the elephant can breed, false otherwise.
     */
    public boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
}
