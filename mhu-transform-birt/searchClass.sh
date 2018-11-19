#!/bin/bash

cd src/main/resources/ReportEngine/lib

for i in $(ls *.jar); do
  echo $i
  unzip -t $i | grep $1
  echo
done


