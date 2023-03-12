#!/bin/bash

URL=$(curl -s https://api.github.com/repos/razielez/chatgpt-kt-bot/releases/latest | jq '.assets | .[] | .browser_download_url' | grep jar | cut -d'"' -f2 | head -1)
echo "url: $URL"
JAR=chatgpt-kt-bot.jar
wget "$URL"
nohup java -jar -Xmx10m $JAR &

