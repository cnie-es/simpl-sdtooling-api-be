# Installation Guide

## Overview
This guide will help you install and run **sdtooling-api-be** on a fresh system. Follow the steps for your operating system and refer to troubleshooting if you encounter issues.

---

## Prerequisites

- **Java**: JDK 17 or higher
- **Maven**: 3.8.x or higher
- **Git**: Latest stable version
- **Operating System**: Windows, macOS, or Linux
- **Internet Connection**: Required for downloading dependencies

> **Note:** Ensure your `JAVA_HOME` and `MAVEN_HOME` environment variables are set correctly.

---

## Step-by-Step Installation

### 1. Clone the Repository

```
git clone https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be.git
cd sdtooling-api-be
```

### 2. Build the Project

#### Windows
Run:
```
#build_and_release.bat
```

#### macOS/Linux
Run:
```
chmod +x #build_and_release.bat
./#build_and_release.bat
```

_Alternatively, use Maven directly:_
```
mvn clean install
```

### 3. Run the Application

Refer to the documentation or run the generated JAR/WAR file as per your deployment needs.

---

### 4. Verify Installation

To verify the application is running correctly, call the `/status` endpoint:

```
curl http://localhost:<port>/status
```

Replace `<port>` with the actual port number configured for the application.

---

## Example Output

After a successful build, you should see output similar to:
```
[INFO] BUILD SUCCESS
```

---

## Configuration

The application behaviour is controlled through configuration parameters.
Configuration values can be provided via:

- Environment variables
- Helm values (if deployed via Kubernetes)
- application.yml overrides

### Configuration Parameters
##### Core Service Configuration
###### This section defines the core runtime settings of the service, including ports, domain resolution and environment identifiers.
---------------
| Parameter name         | Description                       | Default value       | Allowed values/range | Minimum version | Status |
| ---------------------- | --------------------------------- | ------------------- | -------------------- | --------------- | ------ |
| servicePort            | Service port exposed internally   | 8080                | integer (1–65535)    | 1.22.0           | Active |
| containerPort          | Container listening port          | 8080                | integer (1–65535)    | 1.22.0           | Active |
| vaultEnvIdentifier     | Vault environment identifier      | dev                 | string               | 1.22.0           | Active |
| domainUrlEnvIdentifier | Domain URL environment identifier | dev                 | string               | 1.22.0           | Active |
| domain                 | Cluster domain                    | dev.simpl-europe.eu | valid DNS            | 1.22.0           | Active |

##### Logging Configuration
###### This section defines logging behaviour, including log levels and dynamic configuration reload.
---------------
| Parameter name        | Description                            | Default value           | Allowed values/range            | Minimum version | Status |
| --------------------- | -------------------------------------- | ----------------------- | ------------------------------- | --------------- | ------ |
| log4j.config          | Path to log4j2 configuration file      | file:/config/log4j2.xml | valid file path                 | 1.22.0           | Active |
| log4j.monitorInterval | Log4j config reload interval (seconds) | 10                      | integer ≥ 0                     | 1.22.0           | Active |
| log4j.level.root      | Root logging level                     | INFO                    | TRACE, DEBUG, INFO, WARN, ERROR | 1.22.0           | Active |
| log4j.level.app       | Application logging level              | INFO                    | TRACE, DEBUG, INFO, WARN, ERROR | 1.22.0           | Active |
| log4j.level.feign     | Feign client logging level             | INFO                    | TRACE, DEBUG, INFO, WARN, ERROR | 1.22.0           | Active |

##### OpenAPI configuration
###### This section defines OpenAPI/Swagger exposure settings used for API documentation and service discovery.
---------
| Parameter name        | Description                                                 | Default value                                                                                      | Allowed values/range | Minimum version | Status |
| --------------------- | ----------------------------------------------------------- | -------------------------------------------------------------------------------------------------- | -------------------- | --------------- | ------ |
| openapiConfig.servers | Comma-separated list of server URLs used by Swagger/OpenAPI | [https://creation-wizard-api.dev.simpl-europe.eu](https://creation-wizard-api.dev.simpl-europe.eu) | valid URL(s)         | 1.22.0           | Active |

##### Observability (Open Telemetry)
###### This section defines telemetry and distributed tracing configuration for monitoring and observability.
---------
| Parameter name                      | Description               | Default value                                                                                  | Allowed values/range | Minimum version | Status |
| ----------------------------------- | ------------------------- | ---------------------------------------------------------------------------------------------- | -------------------- | --------------- | ------ |
| openTelemetry.disabled              | Disable OpenTelemetry SDK | true                                                                                           | true/false           | 1.22.0           | Active |
| openTelemetry.environment           | Logical environment name  | local-dev                                                                                      | string               | 1.22.0           | Active |
| openTelemetry.otlpExporter.endpoint | OTLP collector endpoint   | [http://collector.common01.dev.simpl-europe.eu](http://collector.common01.dev.simpl-europe.eu) | valid URL            | 1.22.0           | Active |
| openTelemetry.otlpExporter.protocol | OTLP protocol             | http/protobuf                                                                                  | http/protobuf, grpc  | 1.22.0           | Active |
| openTelemetry.propagators           | Context propagation type  | tracecontext                                                                                   | tracecontext         | 1.22.0           | Active |
##### Storage configuration
###### This section defines persistent storage configuration for schema management and file storage.
---------
| Parameter name                   | Description                                 | Default value                | Allowed values/range  | Minimum version | Status |
| -------------------------------- | ------------------------------------------- | ---------------------------- | --------------------- | --------------- | ------ |
| schemaVolumeClaim                | PersistentVolumeClaim ID for schema storage | nfs-storage-pvc              | valid PVC name        | 1.22.0           | Active |
| schemaPvcMountPath               | Mount path for schema storage               | /home/simpluser/data         | valid filesystem path | 1.22.0           | Active |
| schemaSyncService.repositoryPath | Path to synced schema repository            | /home/simpluser/data/schemas | valid filesystem path | 1.22.0           | Active |

##### Resource configuration
###### This section defines Kubernetes resource allocation (CPU and memory requests/limits).
---------
| Parameter name            | Description    | Default value | Allowed values/range     | Minimum version | Status |
| ------------------------- | -------------- | ------------- | ------------------------ | --------------- | ------ |
| resources.requests.cpu    | CPU request    | 0m            | Kubernetes CPU format    | 1.22.0           | Active |
| resources.requests.memory | Memory request | 0Gi           | Kubernetes memory format | 1.22.0           | Active |
| resources.limits.cpu      | CPU limit      | 1000m         | Kubernetes CPU format    | 1.22.0           | Active |
| resources.limits.memory   | Memory limit   | 2Gi           | Kubernetes memory format | 1.22.0           | Active |

##### Ingress configuration
###### This section defines ingress exposure and TLS configuration for development environments.
---------
| Parameter name        | Description             | Default value | Allowed values/range | Minimum version | Status |
| --------------------- | ----------------------- | ------------- | -------------------- | --------------- | ------ |
| ingress.enabled       | Enable ingress exposure | false         | true/false           | 1.22.0           | Active |
| ingress.clusterIssuer | Cluster issuer name     | dev-prod      | string               | 1.22.0           | Active |
| ingress.ingressClass  | Ingress class           | nginx         | string               | 1.22.0           | Active |

##### Security Configuration
###### This section defines security-related configuration, including CORS and JWT enforcement.
---------
| Parameter name                   | Description                        | Default value              | Allowed values/range         | Minimum version | Status |
| -------------------------------- | ---------------------------------- | -------------------------- | ---------------------------- | --------------- | ------ |
| web.mvc.cors.allowedOrigins      | Allowed CORS origins               | ""                         | comma-separated list or *    | 1.22.0           | Active |
| web.mvc.bearerToken.required     | Require JWT Bearer token           | true                       | true/false                   | 1.22.0           | Active |
| web.mvc.bearerToken.allowedPaths | Paths excluded from JWT validation | /actuator/health/*,/status | comma-separated ant patterns | 1.22.0           | Active |

##### External service connectivity
###### This section defines URLs and routing configuration for dependent internal services.
---------
| Parameter name                             | Description                               | Default value                                                                                                                                                  | Allowed values/range | Minimum version | Status |
| ------------------------------------------ | ----------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------- | --------------- | ------ |
| authenticationProvider.service.url         | URL of Authentication Provider service (internal by Service)    | [http://authentication-provider.gaiax-edc-dev-sd.svc.cluster.local:8080](http://authentication-provider.gaiax-edc-dev-sd.svc.cluster.local:8080)               | valid URL            | 1.22.0           | Active |
| connectorAdapter.service.url               | URL of Connector Adapter service (internal by Service)          | [http://edc-connector-adapter-provider.gaiax-edc-dev-sd.svc.cluster.local:8080](http://edc-connector-adapter-provider.gaiax-edc-dev-sd.svc.cluster.local:8080) | valid URL            | 1.22.0           | Active |
| catalogueAdapter.tier2Gateway.url          | Tier2 gateway URL for catalogue adapter   | [https://t2.gaiax-edc-dev-catalogue.dev.simpl-europe.eu](https://t2.gaiax-edc-dev-catalogue.dev.simpl-europe.eu)                                               | valid HTTPS URL      | 1.22.0           | Active |
| catalogueAdapter.tier2Gateway.pathPrefix   | Route prefix for catalogue adapter        | /catalogue-adapter-service                                                                                                                                     | valid path           | 1.22.0           | Active |
| federatedCatalogue.tier2Gateway.url        | Tier2 gateway URL for federated catalogue | [https://t2.gaiax-edc-dev-catalogue.dev.simpl-europe.eu](https://t2.gaiax-edc-dev-catalogue.dev.simpl-europe.eu)                                               | valid HTTPS URL      | 1.22.0           | Active |
| federatedCatalogue.tier2Gateway.pathPrefix | Route prefix for federated catalogue      | /fc-service                                                                                                                                                    | valid path           | 1.22.0           | Active |
| validation.config.enabled                  | Enable validation service integration     | true                                                                                                                                                           | true/false           | 1.22.0           | Active |
| validation.config.domain                   | URL of validation service (internal by Service)                 | [http://sdtool-validation:8080](http://sdtool-validation:8080)                                                                                                 | valid URL            | 1.22.0           | Active |

##### Hash configuration
###### This section defines hashing behaviour and template/model mappings used for contract, sla and billing document integrity verification.
---------
| Parameter name        | Description                              | Default value   | Allowed values/range | Minimum version | Status |
| --------------------- | ---------------------------------------- | --------------- | -------------------- | --------------- | ------ |
| hash.config.algorithm | Hash algorithm                           | SHA256          | SHA256, SHA512       | 1.22.0           | Active |
| hash.config.templates | JSON array defining hashable templates   | See values.yaml | valid JSON array     | 1.22.0           | Active |
| hash.config.models    | JSON array defining hash model structure | See values.yaml | valid JSON array     | 1.22.0           | Active |

##### Offering Type configuration
###### This section defines mapping rules between shape file names and offering types.
---------
| Parameter name                | Description                                         | Default value   | Allowed values/range | Minimum version | Status |
| ----------------------------- | --------------------------------------------------- | --------------- | -------------------- | --------------- | ------ |
| offeringType.config.templates | Mapping between shape file names and offering types | See values.yaml | valid JSON array     | 1.22.0           | Active |


> Environment variables override default values.
> When deployed via Helm, values are injected through values.yaml.

## Troubleshooting

- **Java not found**: Ensure Java is installed and `JAVA_HOME` is set.
- **Maven not found**: Install Maven and add it to your PATH.
- **Permission denied (macOS/Linux)**: Run `chmod +x #build_and_release.bat`.
- **Dependency download errors**: Check your internet connection and proxy settings.

---

## Automated Options

- Use the provided `#build_and_release.bat` script for automated build and release.
- For Dockerized deployments, check for a `Dockerfile` or related scripts in the repository.

---

## Version Information

- **sdtooling-api-be**: v1.0.0 or higher
- **Java**: 17+
- **Maven**: 3.8+
