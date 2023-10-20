set positional-arguments

help:
    @just --list --unsorted

clean:
    ./gradlew clean 

build:
    ./gradlew clean build -q -x test
alias b := build

run:
    ./gradlew clean bootRun