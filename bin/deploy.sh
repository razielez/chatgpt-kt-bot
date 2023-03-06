#!/bin/zsh


./gradlew clean build -q -x test
target=root@ali:/root/chatgpt-kt-bot/
version=0.1.0
distributions_path=./app/build/distributions
mv ${distributions_path}/app-${version}.zip ${distributions_path}/app.zip
scp ${distributions_path}/app.zip ${target}
scp ./bin/start.sh ${target}

echo "done"