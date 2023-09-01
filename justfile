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

deploy:
    ./gradlew clean build -q -x test && bin/deploy.sh
alias d := deploy

native: clean
    ./gradlew :app:nativeCompile

run-native:
    ./app/build/native/nativeCompile/app --spring.config.location=file:///`pwd`/conf/application.properties"