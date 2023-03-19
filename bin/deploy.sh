#!/bin/zsh

target=root@ali:/root/chatgpt-kt-bot/
scp ./bin/start.sh ${target}
scp ./app/build/libs/chatgpt-kt-bot.jar ${target}
echo "done"