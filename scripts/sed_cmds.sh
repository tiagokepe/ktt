#!/bin/bash

DIR_TARGET=./results

cd $DIR_TARGET

##g" $f; done
##g" $f; done
for f in $(ls .); do sed -ie "s#\t##g" $f; done
for f in $(ls .); do sed -ie "s/.*}//g" $f; done
for f in $(ls .); do sed -ie "s/.*{//g" $f; done
for f in $(ls .); do sed -ie "s/float //g" $f; done
for f in $(ls .); do sed -ie "s/int //g" $f; done
for f in $(ls .); do sed -ie "s/ [0-9]* [0-9]* = / = /g" $f; done
for f in $(ls .); do sed -ie "s/ [0-9].[0-9]* [0-9].[0-9]* = / = /g" $f; done
#for f in $(ls .); do sed -re "s###g" $f; done
#for f in $(ls .); do sed -ie "s###g" $f; done
#for f in $(ls .); do sed -rie "s###g" $f; done
#for =#g" $f; done
#for =#g" $f; done

rm *e
