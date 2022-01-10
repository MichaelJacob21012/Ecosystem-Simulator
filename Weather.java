import java.util.Random;
/**
 * Possible weather conditions for a simulator.
 *
 * @author Daniel Ratiu and Michael Jacob
 * @version 22/02/2018
 */
public enum Weather
{
    RAINING, SUNNY, WINDY, SNOWING;
    
    // A shared random number generator.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Randomise a weather condition with equal probabilities.
     * @return A random weather condition.
     */
    public static Weather randomWeather(){
        return Weather.values()[rand.nextInt(Weather.values().length)];
    }
    
    /**
     * Randomise a weather condition with weighted probabilities.
     * @return A random weather condition.
     */
    public static Weather randomWeightedWeather(){
        if (rand.nextDouble() <= 0.05){
            return SNOWING;
        }
        else if (rand.nextDouble()  <= 0.18){
            return WINDY;
        }
        else if (rand.nextDouble() <= 0.34){
            return RAINING;
        }
        return SUNNY;
    }
}
