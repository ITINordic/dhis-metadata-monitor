# dhis-metadata-monitor

## NetBeans config
Using JUnit Test Suite with Gradle and NetBeans results in tests being executed multiple times.

Goto Project Properties->Built-in tasks and add this argument to all test tasks "-Dtest.single=MohccSuite"
## HTTP Client Factory and Email Client
For the moment it is not possible to select the client factory or email client from config files

The client factory or email client can be configured using the MonitorConfig.

Implementing this would increase code complexity with little gained as the clients are not expected to be changed frequently

## Jackson deserialiser
This project makes use of jackson json deserialisation

They are known security risks in using jackson deserializer

Please read link below before enabling "ObjectMapper.enableDefaultTyping()"

https://github.com/FasterXML/jackson-docs/wiki/JacksonPolymorphicDeserialization

## Installing Ubuntu 16.04
To create the ubuntu service use the files in src_install

1. Create a user to use for the service using adduser --system --group --disabled-password dhis-metadata-monitor
2. Create systemd unit file /etc/systemd/system/dhis-metadata-monitor.service and specify the user and group created earlier. Example available in src_install
2. Create startup script /usr/local/bin/dhis-metadata-monitor.sh. Example available in src_install
2. Give the user execute permissions on the shell script.
2. Create config folder /etc/dhis-metadata-monitor/
2. Create pid folder /var/run/dhis-metadata-monitor/
2. Give the the service account ownership of the folders using chown dhis-metadata-monitor:dhis-metadata-monitor -R
2. Enable service at startup by using systemctl enable dhis-metadata-monitor.service
2. Start the service using systemctl start dhis-metadata-monitor.service

You can trigger monitoring using curl localhost:4567/monitor.
The service has to be restarted if configurations change.

## Configuring monitor service

The monitor service can be configured by placing configuration files in /etc/dhis-metadata-monitor/ 
or the home directory of the service user e.g. /home/dhis-metadata-monitor.

Configurations can be specified as either properties or json files named config.properties or config.json respectively.

Account information to access DHIS should be placed in secret.properties placed the service user's home directory.

### config.properties syntax
```
apiRootUrl=https://dhis2.example.org/develop/api
dataset.<group_1>.email=john@example.org
dataset.<group_1>.list=<dataset_1>,<dataset_2>,...
dataset.<group_2>.email=jane@example.org
dataset.<group_2>.list=<dataset_1>,<dataset_3>,...
```
### Sample config.json
```json
{
    "apiRootUrl": "https://dhis2.example.org/develop/api",
    "dataSetGroups": [{
        "name": "<group_1>",
        "email": "john@example.org",
        "dataSets": [
            "<dataset_1>",
            "<dataset_2>"
        ]
    },{
        "name": "<group_2>",
        "email": "jane@example.org",
        "dataSets": [
            "<dataset_1>",
            "<dataset_3>"
        ]
    }]
}
```

### Sample secret.properties
```
username=USERNAME
password=PASSWORD
```

