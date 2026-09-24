# Deployment Guide

## Purpose
This guide provides step-by-step instructions and best practices for deploying the `sdtooling-api-be` project into production or controlled environments, including cloud, on-premises, containers, and Kubernetes. It covers environment requirements, configuration, deployment workflows, scaling, monitoring, security, and recovery procedures.

## Component Description
The component enables creating self-descriptions, which can be used to publish and consume services in the GAIA-X ecosystem.

## Prerequisites
- Tier-1 Gateway configuration is required: `sdtooling-api-be` must be protected by the gateway and must not be reached directly from a public address.
- Installation of the IAA authentication-provider component is required
- Installation of the DATA-1 validation-api-be component is required
- Installation of the DATA-1 edc-connector-adapter component (configured with profile `provider`) is required
- Availability of NFS Storage to put schema files

---

## 1. Environment Requirements

- **Infrastructure**: x86_64 architecture, minimum 2 vCPUs, 4GB RAM (production: 4+ vCPUs, 8GB+ RAM recommended).
- **Operating System**: Linux (Ubuntu 20.04+, CentOS 7+, or compatible). Windows containers are not supported.
- **Network**: Outbound internet access for image pulls and updates. Open required ports (see below).
- **Security & Permissions**:
    - Docker/Kubernetes: User must have permissions to manage containers and images.
    - Secrets: No secrets required.

---

## 2. Configuration Details

- **Environment Variables**: Set via Docker/Kubernetes manifests or `.env` files.
- **Config Files**: Mount configuration files as volumes or use Kubernetes ConfigMaps/Secrets.
- **Secrets Management**: No secrets required.

### 2.1 Tier1 Cloud Gateway Integration
In the Tier1 Gateway values.yaml add new routes and rbac rules for the `sdtooling-api-be` application, example:

```yaml
appConfig:
  spring:
    cloud:
      gateway:
         routes:
           - id: sdtooling-api-be
             uri: http://sdtool-be-creation-wizard-api.gaiax-edc-dev-sd.svc.cluster.local:8080
             predicates:
               - Path=/sdtooling-api/**
             filters:
               - StripPrefix=1
  routes:
    public-urls:
      - method: GET
        path: "/sdtooling-api/swagger-ui/**"
      - method: GET
        path: "/sdtooling-api/api-docs/**"
      - method: GET
        path: "/sdtooling-api/status"
        
    rbac:
      - path: "/sdtooling-api/**"
        method: GET
        roles:
          - SD_PUBLISHER
      - path: "/sdtooling-api/**"
        method: POST
        roles:
          - SD_PUBLISHER
```
**Note**
- http://sdtool-be-creation-wizard-api.gaiax-edc-dev-sd.svc.cluster.local:8080 must be changed to your `sdtooling-api-be` service url
- rbac roles must be adapted to your needs

---

## 3. Step-by-Step Deployment

### A. Docker
1. **Build the image** (if not using a published one):
   ```sh
   docker build -t sdtooling-api-be:latest .
   ```
2. **Run the container**:
   ```sh
   docker run -d --name sdtooling-api-be \
     -p 8080:8080 \
     sdtooling-api-be:latest
   ```

### B. Kubernetes (using Helm charts)
1. **Configure values** in `charts/values.yaml` (or override via CLI):
    - Required Helm variables to configure:
      ```yaml
      # Logging configuration
      log4j.config: <value>
      
      # OpenAPI configuration
      openapiConfig.servers: <value>
      
      # OpenTelemetry configuration
      openTelemetry.otlpExporter.endpoint: <value>
      openTelemetry.otlpExporter.protocol: <value>
      openTelemetry.propagators: <value>
      openTelemetry.environment: <value>
      openTelemetry.disabled: <value>
      
      # CORS configuration
      web.mvc.cors.allowedOrigins: <value>
      web.mvc.bearerToken.required: <value>
      web.mvc.bearerToken.allowedPaths: <value>

      # ID of persistentVolumeClaims associated with NFS Storage
      schemaVolumeClaim: <value>
      # Mount path used for schema storage
      schemaPvcMountPath: <value>
      
      # Url to the IAA AuthenticationProvider internal service
      authenticationProvider.service.url: <value>
      # Url to the connector adapter internal service
      connectorAdapter.service.url: <value>
      # Url to the tier2 gateway for the federated catalog service
      federatedCatalogue.tier2Gateway.url: <value>
      # Tier2 gateway path configured as route rule for the federated catalog service
      federatedCatalogue.tier2Gateway.pathPrefix: <value>
      
      # Configuration for connectivity between SD-Tooling-Api-Be and SD-Tooling-Api-Validation
      validation.config.enabled: <value>
      validation.config.domain: <value>
      validation.config.validationApi: <value>
      validation.config.validationJsonldApi: <value>
      ```
2. **Deploy with Helm**:
   ```sh
   helm install sdtooling-api-be ./charts
   ```
3. **Setup with ArgoCD**:
    - Create an ArgoCD application pointing to your Helm chart repository
    - Configure sync options to use the Helm chart values
    - Set the target namespace and cluster for deployment
    - Enable automated sync if desired for continuous deployment

4. **Verify deployment**:
   ```sh
   kubectl get pods -l app=sdtooling-api-be
   kubectl logs <pod-name>
   ```

5. **Check startup status**:
    - Verify the service is running correctly by calling the `/status` endpoint

### C. Cloud Platforms
- Use managed Kubernetes (EKS, AKS, GKE) or container services (ECS, ACI) as above.
- Ensure cloud firewalls/security groups allow required ports.

---

## 4. Scaling and Monitoring

- **Scaling**:
    - Docker: Run multiple containers behind a load balancer (e.g., NGINX, HAProxy).
    - Kubernetes: Set `replicaCount` in Helm values or use Horizontal Pod Autoscaler.
- **Monitoring**:
    - Integrate with Prometheus, Grafana, or cloud-native monitoring.
    - Expose health endpoints and configure liveness/readiness probes in Kubernetes.

---

## 5. Security Best Practices

- **Network**: Restrict inbound traffic to required ports (default: 8080/tcp).
- **User Access**: A valid JWT token from Keycloak is required for service invocation.
- **Updates**: Regularly update base images and dependencies.

---

## 6. Rollback and Recovery

- **Docker**: Stop and remove the faulty container, then redeploy the previous image tag.
- **Kubernetes**: Use `helm rollback` or `kubectl rollout undo` to revert to a previous release.
- **Backups**: Regularly back up configuration, secrets, and persistent data.
- **Logs**: Aggregate logs for troubleshooting (e.g., ELK stack, cloud logging).

---

## References
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Helm Documentation](https://helm.sh/docs/)
