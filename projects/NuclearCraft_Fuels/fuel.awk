BEGIN { FS = "_" }
/^D:fission/, $0 == ">" {
  if ($2) { # start record
    sub(/ </, "");
    fuel = $2;
    type = $3 == "fuel" ? "time" : $3;
    i = 0;
    next;
  }
  if ($0 == ">") { # end record
    next;
  }
  
  registry[fuel][i][type] = $0;
  i++;
}
END {
  types[1] = "heat";
  types[2] = "power";
  types[3] = "time";
  types[4] = "radiation";

  printf "fuel,meta,"
  for (i = 1; i < length(types); i++) {
    printf "%s,", types[i];
    
  }
  printf "%s\n", types[length(types)];
  
  for (fuel in registry) {
    for (value in registry[fuel]) {
      printf "%s,%s", fuel, value;
      for (i = 1; i < length(types) + 1; i++) {
        printf ",%s", registry[fuel][value][types[i]]
      }
      printf "\n";;
    }
  }
}
