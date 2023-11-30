#include "reactor.h"

int main(char args[]) {

}

ReactorVars step(ReactorVars variables, ReactorConsts constants) {
  double coreSat = (double) variables.saturation / (double)constants.maxSaturation;
  double negCSat = (1 - (double)coreSat) * 95;
  double temp50 = min((variables.temperature / MAX_TEMPERATURE) * 50, 99);
  double tFuel = variables.convertedFuel + variables.reactableFuel;
  double convLVL = ((variables.convertedFuel / tFuel) * 1.3) - 0.3;

  double tempRiseExpo = (negCSat * negCSat * negCSat) / (100 - negCSat) + TEMP_OFFSET;
  double tempRiseResist =
