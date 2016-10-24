#!/usr/bin/env sh

cd sign
wget http://dl.weifruit.cn/fruitexp.keystore
wget http://dl.weifruit.cn/keystore.properties.enc
openssl aes-256-ecb -d -k $KEYSTORE_PASSWORD -in keystore.properties.enc -out keystore.properties
cd ..
