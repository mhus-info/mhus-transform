#!/bin/bash

embed=
cd src/main/resources/ReportEngine/lib

for i in $(ls *.jar); do
  rm -r ../../../../../target/META-INF
  unzip -d ../../../../../target $i META-INF/MANIFEST.MF >/dev/null
  group=$(cat ../../../../../target/META-INF/MANIFEST.MF | grep Bundle-SymbolicName|cut -d : -f 2 | cut -d \; -f 1 | cut -b 2-|tr -d '\r')
  version=$(cat ../../../../../target/META-INF/MANIFEST.MF | grep Bundle-Version|cut -d : -f 2|cut -b 2-|tr -d '\r')
  artifact=$group
  embed="$embed,$artifact"
  echo "<!-- $i -->"
  echo "<dependency>"
  echo "  <groupId>$group</groupId>"
  echo "  <artifactId>$artifact</artifactId>"
  echo "  <version>$version</version>"
  echo "  <scope>system</scope>"
  echo "  <systemPath>\${project.basedir}/src/main/resources/ReportEngine/lib/$i</systemPath>"
  echo "</dependency>"
done

echo
echo "<Embed-Dependency>$embed</Embed-Dependency>"

rm -r ../../../../../target/META-INF

