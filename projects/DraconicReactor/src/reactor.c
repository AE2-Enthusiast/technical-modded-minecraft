#include "reactor.h"

int main(char args[]) {

}

void subdivide(Square face) {
  

Dependents calculate(Independents variables, Constants constants) {
  Dependents output;
  double coreSat = (double)variables.saturation / (double)constants.maxSaturation;
  double negCSat = (1 - (double)coreSat) * 95;
  double temp50 = min((variables.temperature / MAX_TEMPERATURE) * 50, 99);
  double tFuel = variables.convertedFuel + variables.reactableFuel;
  double convLVL = ((variables.convertedFuel / tFuel) * 1.3) - 0.3;

  double tempRiseExpo = (negCSat * negCSat * negCSat) / (100 - negCSat) + TEMP_OFFSET;
  double tempRiseResist = (temp50 * temp50 * temp50 * temp50) / (100 - temp50);
  double riseAmount = (tempRiseExpo - (tempRiseResist * (1 - convLVL)) * convLVL * 1000) / 10000;

  output.tempRise = riseAmount * 10;
  int baseMaxRFt = (int)((constants.maxSaturation / 1000) * OUTPUT_MULTIPLIER * 1.5);
  int maxRFt = (int)(baseMaxRFt * (1 + (convLVL * 2)));
  output.generationRate = (1 - coreSat) * maxRFt;

  double tempDrainFactor = variables.temperature > 8000 ? 1 + ((variables.temperature - 8000) * (variables.temperature - 8000) * 0.0000025) : variables.temperature > 2000 ? 1 : variables.temperature > 1000 ? (variables.temperature - 1000) / 1000 : 0;
  int fieldDrain = min(tempDrainFactor * max(0.01, (1 - coreSat)) * (baseMaxRFt / 10.923556), 1 << 31 - 1);
  output.fieldDrain = fieldDrain;
  //output.fieldDrain = min(fieldDrain, shieldCharge);

  output.fuelUseRate = tempDrainFactor * (1 - coreSat) * (0.001 * constants.reactorFuelUsageMultiplier);
  return output;
}
