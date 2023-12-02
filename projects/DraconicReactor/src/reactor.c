#include <stdio.h>
#include <math.h>
#include <unistd.h>

#include "reactor.h"

Constants constants;
FILE *file;

int main(char args[]) {
  init();
  Point start, end;
  Independents p1, p2;
  p1.temperature = 0;
  p1.saturation = 0;
  start.value = getValue(calculate(p1));
  p2.temperature = 25000;
  p2.saturation = constants.maxSaturation;
  end.value = getValue(calculate(p2));
  start.position = p1;
  end.position = p2;
  subdivide(start, end, 25000);
}

double getValue(Dependents d) {
  return d.fuelUseRate;
}

// ortho is the distance along the axis between the two points
void subdivide(Point p1, Point p2, int ortho) {
  if (ortho == 0)
    return;
  Point mid = avg(p1, p2);
  double real = getValue(calculate(mid.position));
  // Here's where the dependent variable is picked
  // double error = fabs((mid.value - real.fuelUseRate) / (1 +
  // real.fuelUseRate));
  double error = fabs((mid.value - real) / (mid.value + real));
  mid.value = real;
  
  //printf("(%lf, %ld) / (%lf, %ld) = %lf, Error: %lf\n",
  //p1.position.temperature, p1.position.saturation, p2.position.temperature,
  //p2.position.saturation, mid.value, error);
  fwrite(&mid.position.temperature, sizeof(mid.position.temperature), 1, file);
  fwrite(&mid.position.saturation, sizeof(mid.position.saturation), 1, file);
  fwrite(&mid.value, sizeof(mid.value), 1, file);
  
  if (error > MAX_ERROR * 2) {
    //sleep(1);
    subdivide(p1, mid, ortho / 2);
    subdivide(mid, p2, ortho / 2);
    p1.position.temperature += ortho;
    p2.position.temperature -= ortho;
    p1.value = getValue(calculate(p1.position));
    p2.value = getValue(calculate(p2.position));
      subdivide(mid, p1, ortho / 2);
    subdivide(p2, mid, ortho / 2);
  }
}

void init() {
  constants.convertedFuel = SIZE * CONVERSION_LEVEL;
  constants.reactableFuel = SIZE - constants.convertedFuel;
  constants.maxShieldCharge = SIZE * 96.45061728395062 * 100;
  constants.maxSaturation = (int)(SIZE * 96.45061728395062 * 1000);

  constants.reactorOutputMultiplier = 500;
  constants.reactorFuelUsageMultiplier = 10;

  file = fopen("graph", "wb");
}

Point avg(Point p1, Point p2) {
  Point m;
  Independents position;
  position.temperature = (p1.position.temperature + p2.position.temperature) / 2;
  position.saturation = (p1.position.saturation + p2.position.saturation) / 2;
  m.position = position;
  m.value = (p1.value + p2.value) / 2;
  return m;
}

Dependents calculate(Independents variables) {
  
  Dependents output;
  double coreSat = (double)variables.saturation / (double)constants.maxSaturation;
  double negCSat = (1 - (double)coreSat) * 95;
  double temp50 = min((variables.temperature / MAX_TEMPERATURE) * 50, 99);
  double tFuel = constants.convertedFuel + constants.reactableFuel;
  double convLVL = ((constants.convertedFuel / tFuel) * 1.3) - 0.3;

  double tempRiseExpo = (negCSat * negCSat * negCSat) / (100 - negCSat) + TEMP_OFFSET;
  double tempRiseResist = (temp50 * temp50 * temp50 * temp50) / (100 - temp50);
  double riseAmount = (tempRiseExpo - (tempRiseResist * (1 - convLVL)) * convLVL * 1000) / 10000;

  output.tempRise = riseAmount * 10;
  variables.temperature += riseAmount * 10;
  
  int baseMaxRFt = (int)((constants.maxSaturation / 1000) * constants.reactorOutputMultiplier * 1.5);
  int maxRFt = (int)(baseMaxRFt * (1 + (convLVL * 2)));
  output.generationRate = (1 - coreSat) * maxRFt;

  double tempDrainFactor = variables.temperature > 8000 ? 1 + ((variables.temperature - 8000) * (variables.temperature - 8000) * 0.0000025) : variables.temperature > 2000 ? 1 : variables.temperature > 1000 ? (variables.temperature - 1000) / 1000 : 0;
  int fieldDrain = min(tempDrainFactor * max(0.01, (1 - coreSat)) * (baseMaxRFt / 10.923556), 2147483647);
  output.fieldDrain = fieldDrain;
  //output.fieldDrain = min(fieldDrain, shieldCharge);

  output.fuelUseRate = tempDrainFactor * (1 - coreSat) * (0.001 * constants.reactorFuelUsageMultiplier);
  return output;
  

  //Dependents output;
  //output.fuelUseRate = variables.temperature * variables.temperature + (double)variables.saturation * variables.saturation;
  //return output;
}

