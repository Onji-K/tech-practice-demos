# Spring Cloud Config Demo

## Config Server

### Request

http://{host}:{port}/application/test

### Response

200 OK

```json
{
    "name": "application",
    "profiles": [
        "test"
    ],
    "label": null,
    "version": "59eda5437f27f7fb825e27b0c7a18a53d4ed88db",
    "state": null,
    "propertySources": [
        {
            "name": "https://github.com/Onji-K/tech-practice-demos.git/spring-cloud-config-demo/configTarget/application-test.yml",
            "source": {
                "spring.datasource.uri": "myEnvironment",
                "spring.datasource.username": "root",
                "spring.datasource.password": "password"
            }
        }
    ]
}
```