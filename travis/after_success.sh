#!/usr/bin/env sh

if [ -n $TRAVIS_TAG ]; then
	curl --ftp-ssl -k -T app/build/outputs/apk/app-release.apk -u $FTP_USER:$FTP_PASSWD ftp://weifruit.cn:8021/html/app/FruitExp-${TRAVIS_TAG}.apk
	curl --ftp-ssl -k -T changelog/$TRAVIS_TAG.txt -u $FTP_USER:$FTP_PASSWD ftp://weifruit.cn:8021/html/app/changelog-${TRAVIS_TAG}.txt

	changelog="$(cat changelog/$TRAVIS_TAG.txt)"
	postdata="changelog=$changelog&nonce_str=$RANDOM&version=$TRAVIS_TAG"
	sign=`echo -n "$postdata&key=$FTP_PASSWD" | openssl md5 | sed 's/^.* //'`
	curl -v -d "$postdata&sign=$sign" https://weifruit.cn/extension/module/download/api/release.php
fi
