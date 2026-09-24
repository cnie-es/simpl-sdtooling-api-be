# SD Tooling BE

> ⚠️ **Modified work — CNIE-ES fork.**
> This repository is **not** the original SIMPL sdtooling-api-be. It is a derivative work based on
> the upstream `1.25.0` development line (commit `744ede78`,
> [upstream](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be)),
> modified by the EDNEL-RIOJA project team for CNIE-ES between **2026-05-21 and 2026-09-17** to add
> multilingual self-description schemas, the REST_API and AMAZON_S3 sources and EDVAL property
> enrichment. Distributed as `1.25.0-edval` under the same terms as the original work. Full details
> of what was changed and when: [NOTICE.EDNEL.md](NOTICE.EDNEL.md).

> **Purpose**: The SD-Tooling component is designed to provide API services that enable the generation, validation, and publication of self-descriptions within the federated catalogue. Its main objective is to facilitate the creation of compliant self-descriptions and ensure their correctness before making them discoverable across the Simpl-Open ecosystem.

---

## 📑 Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [⚡ Quick Start](#-quick-start)
   - [Use as a Dependency](#use-as-a-dependency)
   - [Run Locally](#run-locally)
4. [Installation guide](#installation--guide)
5. [Configuration](#configuration)
5. [User Guide](#user--guide)
6. [Testing](#testing)
7. [Contributing](#contributing)
8. [Contact & Support](#contact--support)

---

## Overview

The **SD-Tooling** component provides REST services that enable the **creation, validation, and publication** of self-descriptions within the **federated catalogue**. Its main purpose is to support data providers in defining all the required metadata, policies, and technical attributes associated with an asset before making it discoverable in the Simpl-Open ecosystem. 
Through its APIs, the component exposes the **self-description schema** to the frontend interface, allowing users to visualize and fill in the required fields. It also provides dedicated endpoints for defining **access and usage policies** and for specifying the **Resource Address**, which identifies the physical or logical location of the shared asset on the provider side.

**Key Features**
- REST API endpoints for generation, validation, and publication of self-descriptions.
- Exposure of the self-description schema to the frontend, enabling dynamic rendering of input fields.
- API support for defining access and usage policies related to shared assets.
- Management of the Resource Address, specifying the source location of the asset.
- Registration of asset information on the Provider Connector, including the insertion of technical attributes into the self-description.
- Automated publication of validated self-descriptions in the federated catalogue.
- Logging, auditing, and error handling to ensure governance and traceability across the publishing workflow.

**Relation to other Simpl-Open agents or modules**

The component is **exposed through the Tier-1 Gateway** and communicates with the **Tier-2 Gateway** hosted on the **Governance Authority Agent** to execute  publication process.
Within the **Provider Agent**, the **SD-Tooling** component also interacts directly with the **Provider Connector** to register the asset and handle the technical lifecycle of the self-description up to its publication in the federated catalogue.

---

## Prerequisites

```bash
Java 21+
Maven 3.9+
Access to EU GitLab Package Registry (for repo declared in POM file)
IDE with plugin Lombok enabled (IntelliJ/Eclipse/VS Code)
Enabled connectivity with Tier-1/Tier-2 Gateway
Enabled connectivity with provider EDC Connector
```

---

## ⚡ Quick Start

## Installation Guide

The instructions for running the application locally can be found in the following file → [Installation Guide](documents/installation-guide/Installation%20Guide.md)

---

## Configuration

The instructions for setting config parameters can be found in the following file → [Configuration Parameters](documents/installation-guide/Installation%20Guide.md#configuration)

---

## Deployment Guide

The instructions for setting up configuration and deploy in Kubernetes cluster can be found in the following file → [Deployment Guide](documents/deployment-guide/Deployment%20Guide.md)

---

## Upgrade Guide

At the following link, you can find the guide that outlines the changes made in the latest version, including configuration updates, integrations with other systems, and new or modified functionalities, to facilitate the setup of the application within the target environment. → [Upgrade Guide](documents/upgrade-guide/Upgrade%20Guide.md)

## User Manual

At the following link, you can see the API exposed by this microservice → [User Guide](documents/user-manual/User%20Manual.md)

---

## Testing

Testing is covered through the CI/CD pipeline associated with the GIT repository.
This pipeline automatically runs Unit Tests, SAST (Static Application Security Testing) using SonarQube, and security tests performed with Fortify.

---

## Contributing

At the following link, you can find all the information related to the delivery process management adopted for Simpl-Open across its various components.
[Release Management](https://confluence.simplprogramme.eu/display/SIMPL/2050+-+Release+mgnt)

---

## Contact & Support

- **Maintainers**: `Data1 Team`

---

## Licence

The original work, sdtooling-api-be, is © European Union / SIMPL Programme. Its [LICENSE](LICENSE)
declares two licences, the **Apache License Version 2.0** and the **European Union Public Licence
v. 1.2 (EUPL-1.2)**, whose full official texts are now reproduced in that file. This fork complies
with the stricter of the two, the EUPL, and keeps both declarations intact. Third-party components
included in the product are listed in [NOTICE](NOTICE) / [NOTICE.json](NOTICE.json) /
[THIRD_PARTY_LICENCES.md](THIRD_PARTY_LICENCES.md).

This fork is a **modified version** of that work, distributed under the same terms. The
modification notice required by Art. 5 of the EUPL and by Section 4 of the Apache License (what was
modified, by whom and when) is in [NOTICE.EDNEL.md](NOTICE.EDNEL.md). The complete corresponding
source code, including the revision history, is available at
https://github.com/cnie-es/simpl-sdtooling-api-be.

---
