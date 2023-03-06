#!/bin/bash

unzip -o app.zip
app_path=./app-0.1.0
ITEM_NAME=app
killall java
nohup ${app_path}/bin/app start &
