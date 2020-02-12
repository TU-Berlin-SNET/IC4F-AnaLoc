#!/bin/bash
find . -maxdepth 2 -name "*Service" -type d -print0 |
    while IFS= read -r -d $'\0' line; do
        cd $line && ./build.sh && cd ../..
        #cd $line && dos2unix build.sh && cd ../..
    done
    