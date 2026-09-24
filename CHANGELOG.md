## 1.25.0-edval (2026-09-17)

> Derivative work by the **EDNEL-RIOJA** project team for **CNIE-ES**, based on the upstream
> sdtooling-api-be `1.25.0` development line (commit `744ede78`). Modified between
> **2026-05-21 and 2026-09-17**, published under the same terms as the original work. See
> [NOTICE.EDNEL.md](NOTICE.EDNEL.md) for the full modification notice.

### Added (2026-05-21 → 2026-09-17)

- **Multilingual self-description schemas**: `ApiSchema`, `CorpusSchema`, `LCRSchema` and
  `ModelSchema` in Catalan, English, Spanish, Basque, Valencian and Galician, each in JSON and Turtle.
- **REST_API source** (template id 14): source template and UI schema for the APPLICATION and DATA
  offer types, registered in `resourceAddress-config.yml` and exposed in the `sharingMethodId`
  allowable values.
- **AMAZON_S3 source with STS** (template id 15): source template and UI schema.
- A `Makefile` with Maven test shortcuts.
- GitHub Actions pipeline building and publishing the container image, on tags or on demand.

### Changed (2026-05-21 → 2026-07-03)

- **EDVAL property enrichment**: `EnrichServiceImpl`, `FederatedCatalogueServiceImpl`, `SDService`,
  `SDServiceImpl` and `ValidationServiceImpl` extended to enrich and validate the EDVAL properties.
- The REST_API sharing method was moved from the APPLICATION to the DATA offer type.
- `BULK_S3` source template and UI schema adjusted alongside the new AMAZON_S3 source.

### Removed (2026-06-04)

- The single-language `ApplicationSchema`, `DataSchema` and `InfraSchema` files, superseded by the
  per-language schemas above. `DataSchema.ttl` was renamed to `ApiSchema_EN.ttl`. This is the only
  change that removes files of the original work.

### Licence compliance (2026-09-10)

The `LICENSE` of the original work declares **two** licences, the Apache License 2.0 and the
EUPL-1.2, without stating how they relate. This fork complies with the stricter of the two, the
EUPL, whose obligations cover those of the Apache License on every overlapping point, and keeps both
declarations intact.

- `NOTICE.EDNEL.md`: modification notice stating that the work has been modified, by whom, when and
  what was changed, with the repository where the complete corresponding source code is available.
  Its per-file tables also satisfy the Apache License requirement to mark the files that changed.
- `README.md`: prominent notice at the top of the file identifying this repository as a modified
  version of sdtooling-api-be, plus a Licence section.
- `LICENSE`: the full official texts of **both** licences are now reproduced in the file, which
  previously only linked to them. The original SIMPL heading and credits line are kept intact.
- `Dockerfile`: OCI image labels (`licenses`, `source`, `vendor`, `description`) and `LICENSE`,
  `NOTICE` and `NOTICE.EDNEL.md` copied into the image, so the notices and the pointer to the
  source code travel with the published container image.
- `pom.xml`: the project coordinates move from `eu.simpl.sdtooling:sdtooling-api-be` to
  `es.cnie.simpl.sdtooling:simpl-sdtooling-api-be`, so that a modified artifact is not identified
  under a namespace belonging to the licensor (EUPL Art. 5, Legal Protection); `scm`, `url` and
  `developers` metadata filled in, and the existing dual `licenses` declaration left untouched. The
  `${env.PROJECT_RELEASE_VERSION}` version is deliberately kept: here it is functional, resolved
  from the build argument the Dockerfile and the pipeline pass in.
- `.github/workflows/build-image-on-tag.yml`: the container image namespace is derived from the
  repository owner instead of being hard-coded, so that the published image and the source code it
  is built from always live in the same organisation, and the licence labels are pinned so that
  `docker/metadata-action` does not infer and override them.
- Helm chart made loadable outside the upstream GitLab pipeline: `Chart.yaml` and `values.yaml`
  carried unsubstituted `${PROJECT_RELEASE_VERSION}` and `${CI_REGISTRY_IMAGE}` placeholders, which
  that pipeline replaced and which made the chart fail to load anywhere else. `image.repository` and
  `image.tag` now have working defaults, overridable at install time, and optional
  `imagePullSecrets` are added, as the published image is private.
- The version of this fork, `1.25.0-edval`, is stated consistently in the chart `version` and
  `appVersion` and in `pipeline.variables.sh`, which the image copies in and which reports the
  running version; both still declared the upstream `1.25.0`.


## 1.24.0 (2026-03-27)

### added (4 changes)

- [[SIMPL-14652](https://jira.simplprogramme.eu/browse/SIMPL-14652) Added new script for HPA](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/9c445429f2baa0a925484ae81a37cc5e26cddf79) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/118))
- [[SIMPL-20780](https://jira.simplprogramme.eu/browse/SIMPL-20780) [BE] Add...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/f5c36d1360d46a123de416a4c5b020bf3ab68fd3) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/118))
- [[SIMPL-19785](https://jira.simplprogramme.eu/browse/SIMPL-19785) [API]...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/72bec72877690ec805ad55f391ec0df802855c57) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/118))
- [[SIMPL-24335](https://jira.simplprogramme.eu/browse/SIMPL-24335) API for...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/4f5f148f7f036e0d79a039e5c6e42e5db8f88092) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/118))

### changed (2 changes)

- [[SIMPL-24437](https://jira.simplprogramme.eu/browse/SIMPL-24437)](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/0509d65e53011cbef077911b44b8b0677137d9c8) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/118))
- [[SIMPL-22519](https://jira.simplprogramme.eu/browse/SIMPL-22519) Create...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/698e5a97ddcc2c085d1a69ca9fe0232ed5b26395) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/113))

### fixed (2 changes)

- [[SIMPL-23923](https://jira.simplprogramme.eu/browse/SIMPL-23923) Wrong log...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/a53c571aad208e290e345d38d4c06ef7dbafe563) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/118))
- [[SIMPL-23681](https://jira.simplprogramme.eu/browse/SIMPL-23681) [API...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/df3cab3e28b266758a55de0cd6385c1480115a6f) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/113))


## 1.15.2 (2025-09-15)

### changed (1 change)

- [simpl-data1-common upgraded to 1.4.1 to enable request uri tracing in](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/e50da5993c8cb3ab1690bb90dfb5121f353bc20b) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/90))

### fixed (1 change)

- [[SIMPL-16767](https://jira.simplprogramme.eu/browse/SIMPL-17752) SD-UI](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/01c2671550cdbe22b191a35c572dafe66145ea90) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/90))



## 1.15.1 (2025-09-09)

No changes.



## 1.15.0 (2025-09-04)

### added (3 changes)

- [[SIMPL-17313](https://jira.simplprogramme.eu/browse/SIMPL-17313) Create...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/a3469b1f4b3f7889cf377ddab5d54a479eb1d403) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/84))
- [[SIMPL-15584](https://jira.simplprogramme.eu/browse/SIMPL-15584) API to...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/4c669da24099d754e58d02814347f8bcf1fdd532) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/84))
- [[SIMPL-15289](https://jira.simplprogramme.eu/browse/SIMPL-15289) Automatically...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/e0ba3d00c69d551f009ce236a1ba0bf6fd8f00da) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/84))

### fixed (4 changes)

- [[SIMPL-17453](https://jira.simplprogramme.eu/browse/SIMPL-17453) SD](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/c156b05f08ca8a7648bb91723befbee272048a19) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/84))
- [[SIMPL-17453](https://jira.simplprogramme.eu/browse/SIMPL-17453) SD](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/fb9cb211d59fc1730f5cc16be44ba4ec8088028f) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/84))
- [[SIMPL-8416](https://jira.simplprogramme.eu/browse/SIMPL-8416) Avoid](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/714775c4a07582536514e05aca8d23bf3165d0f3) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/84))
- [[SIMPL-2775](https://jira.simplprogramme.eu/browse/SIMPL-2775) Refactor](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/e240d7c011035f38c2e747fe6e4a55cd7e63bba0) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/84))



## 1.14.1 (2025-08-06)

### fixed (3 changes)

- [[SIMPL-16125](https://jira.simplprogramme.eu/browse/SIMPL-16125) Entry/Acceptance Criteria Report](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/46fd76b6468c9bb8a3d16407928db4bb3d5de8ac) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/81))
- [[SIMPL-16125](https://jira.simplprogramme.eu/browse/SIMPL-16125) Entry/Acceptance Criteria Report](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/37c46760f47127347600c89b347d44cf3384dacb) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/81))
- [[SIMPL-16125](https://jira.simplprogramme.eu/browse/SIMPL-16125) Entry/Acceptance Criteria Report](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/a2d8ea5de60ae4994894974a7711486e80c2c847) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/81))

### changed (1 change)

- [[SIMPL-16125](https://jira.simplprogramme.eu/browse/SIMPL-16125)](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/bb7295c73c1839f12eb6dc96189974dbf1c81b08) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/81))


## 1.14.0 (2025-08-01)

### fixed (9 changes)

- [[SIMPL-14758](https://jira.simplprogramme.eu/browse/SIMPL-14758) [API...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/e1e817455bfb52ec79b4de810250fc61132c2145) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/73))
- [[SIMPL-14757](https://jira.simplprogramme.eu/browse/SIMPL-14757) [API...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/d236390623c8888ee35c667e8fd4e4ec089061d1) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/73))
- [[SIMPL-15891](https://jira.simplprogramme.eu/browse/SIMPL-15891) Hardcoded values in ingresses.](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/0d65bfdd46448a165aea59d77ee1fa06ba184cac) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/73))
- [[SIMPL-14722](https://jira.simplprogramme.eu/browse/SIMPL-14722) Resolve](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/f0c2a197bb177e12e1996f59ef6e949318989993) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/73))
- [[SIMPL-9028](https://jira.simplprogramme.eu/browse/SIMPL-9028) env](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/a86cd2d8fcabaad15a9ac93d5df13eed2f698a81) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/73))
- [[SIMPL-4720](https://jira.simplprogramme.eu/browse/SIMPL-4719) Update](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/b7fb308ca17776dc3afdf2cadcabdd197f2f2421) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/73))
- [[SIMPL-4720](https://jira.simplprogramme.eu/browse/SIMPL-4720) Update](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/64ac97faa6e029d8f736739a6e3478276e71ee53) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/73))
- [[SIMPL-9036](https://jira.simplprogramme.eu/browse/SIMPL-9036) Give a](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/a7a1d2932904ff1bb8bfa6e29e3dc987f634c520) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/73))
- [[SIMPL-10523](https://jira.simplprogramme.eu/browse/SIMPL-10523	Rename](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/c3be962fbd7eb7b7a938d8239fe01b7263f4a477) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/73))


## 1.13.0 (2025-07-11)

### fixed (3 changes)

- [[SIMPL-14116](https://jira.simplprogramme.eu/browse/SIMPL-14116) Resolve](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/62cd1edee0f664434369cf81daead3023cb2dcbe) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/69))
- [[SIMPL-14116](https://jira.simplprogramme.eu/browse/SIMPL-14116) Resolve](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/34d742794b28aff0f880b473387492a3780f91af) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/69))
- [[SIMPL-14116](https://jira.simplprogramme.eu/browse/SIMPL-14116) Resolve](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/d364ada47c7f8d603c499af43bf11bd5872e1d6a) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/69))

### added (1 change)

- [[SIMPL-10562](https://jira.simplprogramme.eu/browse/SIMPL-10562) Extend the SD...](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/9414a5c878110506d6c0e786b1ea98721ca1a6f5) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/69))


## 1.12.2 (2025-07-03)

### changed (1 change)

- [[SIMPL-14932](https://jira.simplprogramme.eu/browse/SIMPL-14932) Update](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/1536e81ddda0411f752bf4673339fff9141c8aab) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/67))


## 1.12.1 (2025-06-30)

### changed (1 change)

- [[SIMPL-13505](https://jira.simplprogramme.eu/browse/SIMPL-13505) Upgrade](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/f403d9a4b024eb06ff1354dccaefa5eec1939f6b) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/65))


## 1.12.0 (2025-06-19)

### added (1 change)

- [[SIMPL-13521](https://jira.simplprogramme.eu/browse/SIMPL-13521) Added ArgoCD manifests.](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/e7c6877624b16052c6b6e98e09b5a3f41186b698) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/63))

### fixed (1 change)

- [[SIMPL-2766](https://jira.simplprogramme.eu/browse/SIMPL-2766) Deleted unused settings.](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/bb8afccbc1cb93ca0fba290c0071381e1ff97a2f) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/63))

### changed (2 changes)

- [error responses aligned to belgif problem model](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/b2dc3f5fd5540bcdd555c3dea8a3d4dbe1158748) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/63))
- [aligned to simpl-data1-common to version 1.1.0 to use belgif Problem](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/39a3097c2db496cf0def257e0bf80d195f53ac94) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/63))


## 1.11.0 (2025-05-29)

### changed (2 changes)

- [[SIMPL-11995](https://jira.simplprogramme.eu/browse/SIMPL-11995)](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/3b3dcfa0f9b009fbfa4428ccb83150b1ebb0ce1b) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/61))
- [[SIMPL-11995](https://jira.simplprogramme.eu/browse/SIMPL-11995)](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/029762ddafb1e0c628db1b3a1fcd5c532ec17710) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/61))

### added (4 changes)

- [[SIMPL-12916](https://jira.simplprogramme.eu/browse/SIMPL-12916) Extend](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/3adc0c79a2e973dc39c302cc2f866952f2b14ff3) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/61))
- [[SIMPL-10562](https://jira.simplprogramme.eu/browse/SIMPL-10562) Added Resource Address config.](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/c5a54a09649d20fd267a0813bb556b63acb00b12) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/61))
- [[SIMPL-12916](https://jira.simplprogramme.eu/browse/SIMPL-12916) Extend](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/e526b9fb69facb3ddcfe99c23c36ebf21a8732d0) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/61))
- [[SIMPL-12999](https://jira.simplprogramme.eu/browse/SIMPL-12999) Config](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/5bc87f0fc214f72debddf48c912b510f716702ac) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/61))

### fixed (1 change)

- [[SIMPL-2775]https://jira.simplprogramme.eu/browse/SIMPL-2775) Refactor](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/bef2ae6d2eb672c4ef56fd047dbd7fbf20703c8c) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/61))


## 1.10.1 (2025-05-09)

### added (1 change)

- [[SIMPL-12186](https://jira.simplprogramme.eu/browse/SIMPL-12186) Enable](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/e97c38eff62c4d1026bce6cfbb024dcf4e045b5d) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/59))


## 1.10.0 (2025-05-07)

### changed (2 changes)

- [[SIMPL-12726](https://jira.simplprogramme.eu/browse/SIMPL-12216)](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/02e6569778e375ec38b33b3c23799f3b033f6cfa) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/57))
- [[SIMPL-12726](https://jira.simplprogramme.eu/browse/SIMPL-12726)](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/119527faea7d1fe738e307a4c6b554a1028fbd38) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/57))


## 1.9.1 (2025-04-16)

### fixed (1 change)

- [Fixed openapi.json](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/77a5676b8c3c535ac34a29e79d2762caf08848a0) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/54))


## 1.9.0 (2025-04-15)

### changed (3 changes)

- [changed values.yaml keys](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/eea53ecb3db94f3817ef497988b4f182effd8e41) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/52))
- [[SIMPL-11419](https://jira.simplprogramme.eu/browse/SIMPL-11419) Update](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/e5db2c07de9cadadb6f033c9199383f0549f6584) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/52))
- [values keys changed: creationWizardApiServicePort changed to](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/ed90ee6d80f726b8f275ac10a60b9711927b1742) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/52))


## 1.8.0 (2025-03-28)

### added (1 change)

- [[SIMPL-10798](https://jira.simplprogramme.eu/browse/SIMPL-10798) Added Spotless and Palantir.](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/6edee09a5cde94c97d255ca43e3b256ba7596897) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/48))

### fixed (3 changes)

- [[SIMPL-2773](https://jira.simplprogramme.eu/browse/SIMPL-2773) Extend](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/b816506bbd0b57cb1ed5e676be4fa65ee84c5bb7) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/48))
- [[SIMPL-2788](https://jira.simplprogramme.eu/browse/SIMPL-2788)](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/d415423ef1924505dc3c2fb753e9727151c8124d) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/48))
- [[SIMPL-4266](https://jira.simplprogramme.eu/browse/SIMPL-4266) Rename](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/838105a38cffc9c8a7dc0804611ff8a2d046ef53) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/48))

### changed (2 changes)

- [[SIMPL-10720](https://jira.simplprogramme.eu/browse/SIMPL-10720)](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/a9afcbea915d536ba56174826ca83433ee257321) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/48))
- [[SIMPL-10720](https://jira.simplprogramme.eu/browse/SIMPL-10720) Implementation of API guidelines](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/b6672702c30a48df3e2e47ee0358f458a19339c9) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/48))

### removed (2 changes)

- [[SIMPL-2827](https://jira.simplprogramme.eu/browse/SIMPL-2827) Removed the unused script.](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/552823c1d3d0014b52e4d4b413453c5d7222f6ee) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/48))
- [[SIMPL-2767](https://jira.simplprogramme.eu/browse/SIMPL-2767) Removed unused Shapes files.](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/commit/869b1b1f9e45fb39fc06b25b00174297f18e145f) ([merge request](https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be/-/merge_requests/48))

