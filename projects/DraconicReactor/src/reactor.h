#ifndef STONE_REACTOR_H
#define STONE_REACTOR_H

#define MAX_TEMPERATURE 10000
#define TEMP_OFFSET 444.7

typedef struct {
  double reactableFuel;
  double convertedFuel;
  double temperature;
  double shieldCharge;
  int saturation;

  double tempDrainFactor;
  double generationRate;
  int fieldDrain;
  double fieldInputRate;
  double fuelUseRate;
} ReactorVars;

typedef struct {
    int maxSaturation;
    double maxShieldCharge;
} ReactorConsts;

ReactorVars step(ReactorVars variables, ReactorConsts constants);

#define min(a, b)\
  a < b ? a : b
