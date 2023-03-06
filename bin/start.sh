#!/bin/bash

unzip -o app.zip
app_path=./app-0.1.0
ITEM_NAME=app
nohup ${app_path}/bin/app start &
