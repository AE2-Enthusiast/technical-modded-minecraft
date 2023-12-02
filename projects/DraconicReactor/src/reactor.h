#ifndef STONE_REACTOR_H
#define STONE_REACTOR_H

#define MAX_ERROR .01

#define SIZE 10368
#define CONVERSION_LEVEL 0

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
  double convertedFuel;
  double reactableFuel;
  double reactorOutputMultiplier;
  double reactorFuelUsageMultiplier;
} Constants;
// The constants will be constant as long as total fuel doesn't change

typedef struct {
  Independents position;
  double value;
} Point;

void init();
double getValue(Dependents d);
Dependents calculate(Independents vars);
void subdivide(Point p1, Point p2, int ortho);
Point avg(Point p1, Point p2);

#define min(a, b)\
  a < b ? a : b

#define max(a, b)\
  a > b ? a : b

#endif
