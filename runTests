#!/bin/bash

# a shellscript to run tests.
# usage: ./runTests or ./runTests TokenTest
if [ $# -lt 1 ] # if there are fewer than 1 argument
then
    java -cp "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/test:bin/" org.junit.runner.JUnitCore BPLCompilerTestSuite
else
    java -cp "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/test:bin/" org.junit.runner.JUnitCore $1 # else just run the class specified by the first argument
fi