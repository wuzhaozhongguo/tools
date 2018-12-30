#!/bin/sh
kills(){
    tpid_file=$1;
    tpid=`cat ${tpid_file}`
    if [[ $tpid ]]; then
        echo 'Kill Process!'
        kill -9 $tpid
	rm -f $tpid_file
    fi
}
kills "tpid"
rm -rf logs/*
