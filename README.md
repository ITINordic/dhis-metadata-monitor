# dhis-metadata-monitor

## NetBeans config
Using JUnit Test Suite with Gradle and NetBeans results in tests being executed multiple times.

Goto Project Properties->Built-in tasks and add this argument to all test tasks "-Dtest.single=MohccSuite"
## Client Factory
For the moment it is not possible to select the client factory from config files

The client factory can be configured using the MonitorConfig.

Implementing this would increase code complexity with little gained as the client is not expected to be changed frequently

## Jackson deserialiser
This project makes use of jackson json deserialisation

They are known security risks in using jackson deserializer

Please read link below before enabling "ObjectMapper.enableDefaultTyping()"

https://github.com/FasterXML/jackson-docs/wiki/JacksonPolymorphicDeserialization
