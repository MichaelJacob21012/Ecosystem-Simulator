# Ecosystem-Simulator
Simulation Description

The project simulates the ecosystem of a small field full of plants and animals.

Daytime lasts 3 steps followed by night-time for 1 step.

Animals move or sleep each step. If there is no space to move, they die. Animals age and die
after reaching a certain age depending on the species. Mature animals will reproduce if there
is a nearby empty cell. Animals that sleep will not move or reproduce but can still die of
hunger or old age.

Animals will eat if their food is in a nearby cell and it is the right time of day. Animals die if
they are eaten. If an animal eats another animal it will move to its prey's cell.

Foxes eat rabbits only at night

Eagles eat rabbits, day and night

Cows eat plants during the day and sleep at night

Elephants eat plants, day and night. They reproduce sexually when two opposite sexes are in
adjacent cells.

Rabbits eat plants, day and night

Extension Tasks

Plants

Plants do not move. They reproduce each step if there is a nearby empty cell. They age and
gain size. Plants lose size when eaten by an animal, larger animals will eat more. Plants die
if eaten completely. Animals will move onto the plant's cell if consumed entirely.
Weather

Weather conditions change each step, the field can be sunny, rainy, windy or snowy, in order
of most probable to least.

Sunny causes plants to grow twice as much.

Rainy causes plants to grow twice as much and eagles get twice as hungry.

Windy kills some plants.

Snowy kills some plants and animals.

The GUI displays the weather condition and day/night of the next step.

Disease

The organisms can be infected and can die from infection each step. Infections are passed
on to members of the same species on adjacent cells. One animal can be randomly infected
every step.
