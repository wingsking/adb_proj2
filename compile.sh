#!/bin/bash
#
#
# compile to directory called "bin"
mkdir -p bin
CP1="src:libs/*"
javac -d bin/ -cp $CP1 src/*/*.java
