#!/bin/bash
#
# Copyright 2018 Mike Hummel
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


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

