#!/bin/sh

for i in 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29
do
    sleep 30 
    javaw -cp lib\cglib.jar:lib\commons-codec-1.3.jar:lib\commons-io-1.3.2.jar:lib\log4j.jar:lib\smack.jar:lib\smackx-mod.jar:bin br.edu.ufcg.lsd.commune.experiments.commune.PeerMain $i > output$i
done
