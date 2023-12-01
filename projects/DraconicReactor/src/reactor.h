#ifndef STONE_REACTOR_H
#define STONE_REACTOR_H

#define MAX_TEMPERATURE 10000
#define TEMP_OFFSET 444.7

typedef struct {
  double temperature;
  //  double shieldCharge;
  int saturation;
} Independents;

typedef struct {
  double tempRise;
  double generationRate;
  int fieldDrain;
  double fieldInputRate; //this'll be set to the real input rate after temp
			 //effects and stuff
  double fuelUseRate;
} Dependents;

typedef struct {
  int maxSaturation;
  double maxShieldCharge;
  double totalFuel;
  double fuelConversion;
  double reactorOutputMultiplier;
  double reactorFuelUsageMultiplier;
} Constants;
// The constants will be constant as long as total fuel doesn't change

/*
 * The squares are stored as center points and half "radii"
 * This makes it easy to derive the center of the subdivisions by
 * adding/subtacting the half radius to the axes of the center point
 */
typedef struct {
  Independent center;
  int distance; // the long awaited square radius
  double value;
} Square;
Dependents calculate(Independents vars, Constants constants);
void subdivide(Square face);

#define min(a, b)\
  a < b ? a : b

#define max(a, b)\
  a > b ? a : b
