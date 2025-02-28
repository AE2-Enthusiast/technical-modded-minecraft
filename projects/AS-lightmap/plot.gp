#!/bin/env gnuplot

set title "splot with \"set pm3d\" (implemented with some terminals)"
set pm3d
set hidden3d
splot 'starlight.dat' every 2::0::12 with lines