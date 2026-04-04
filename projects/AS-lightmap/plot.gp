set title "Heat Map generated from a file containing Z values only"
unset key
# Color runs from black to white
set palette rgbformula 3,3,3
set autoscale xfix
set autoscale yfix
set cbrange [0:1]
set cblabel "Starlight"
set size square
unset colorbox
unset cbtics
unset xtics
unset ytics
set view map
splot 'starlight.dat' matrix with image
pause -1