#!/bin/bash
#
#
# compile to directory called "bin"
mkdir -p bin
CP1="src:libs/*"
CP2="bin:libs/*"
javac -d bin/ -cp $CP1 src/*/*.java
