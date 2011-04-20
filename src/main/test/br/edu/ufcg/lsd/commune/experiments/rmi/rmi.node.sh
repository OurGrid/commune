#!/bin/sh

j=1
for i in 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49
do
    COUNTER=$((($i * 10) + $j))
    RMI_PORT=$((1099 + $COUNTER))
    cd bin
    rmiregistry $RMI_PORT &
    cd .. 
    sleep 30 
    javaw -cp lib\cglib.jar:lib\commons-codec-1.3.jar:lib\commons-io-1.3.2.jar:lib\log4j.jar:lib\smack.jar:lib\smackx-mod.jar:dist/commune-0.1.jar br.edu.ufcg.lsd.commune.experiments.rmi.ReactorMain $COUNTER
done
