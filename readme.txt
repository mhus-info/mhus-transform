====
    Copyright 2018 Mike Hummel

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
====


version=1.0.1-SNAPSHOT
install -s mvn:de.mhus.osgi/mhu-transform-api/${version}
install -s mvn:de.mhus.osgi/mhu-transform-core/${version}
install -s mvn:de.mhus.osgi/mhu-transform-jtwig/${version}
install -s mvn:de.mhus.osgi/mhu-transform-velocity/${version}
install -s mvn:de.mhus.osgi/mhu-transform-freemarker/${version}

install -s mvn:de.mhus.ports/ports-groovy/1.3.5-SNAPSHOT
install -s mvn:de.mhus.osgi/mhu-transform-pdf/${version}

Test installation:

transform:test

======================
 TWIG
======================
>>> Transform successful
======================
 VM
======================
>>> Transform successful
======================
 FTL
======================
>>> Transform successful

Execute transform by command:

transform:transform -p example.twig example.html var=World
