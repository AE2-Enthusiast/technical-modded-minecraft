# NuclearCraft Datamining
Pulls out fuel stats from the NuclearCraft config file. First manually pull out
the section of the config for fission fuels and put them in a separate
file. Then run via `awk -f fuel.awk <fuel config>`. It'll output a CSV to stdout
then.
