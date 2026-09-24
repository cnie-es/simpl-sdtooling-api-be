# Modification notice (EUPL 1.2 Art. 5, Apache License 2.0 §4)

**This is a modified version of SIMPL sdtooling-api-be. It is not the original work.**

The original work, sdtooling-api-be, is part of the SIMPL programme (© European Union / SIMPL
Programme). Its [LICENSE](LICENSE) declares **two** licences, the **Apache License Version 2.0**
and the **European Union Public Licence v. 1.2 (EUPL-1.2)**, without stating how they relate. This
fork therefore complies with the stricter of the two, the EUPL, whose obligations cover those of
the Apache License on every overlapping point, and keeps both declarations intact. The full
official text of both licences is reproduced in [LICENSE](LICENSE). See [NOTICE](NOTICE) /
[NOTICE.json](NOTICE.json) / [THIRD_PARTY_LICENCES.md](THIRD_PARTY_LICENCES.md) for the third-party
components included in the product. All original copyright, licence and disclaimer notices are kept
intact and unmodified in this fork.

## Upstream baseline

| | |
|---|---|
| Original work | sdtooling-api-be (`eu.simpl.sdtooling:sdtooling-api-be`) |
| Upstream repository | https://code.europa.eu/simpl/simpl-open/development/data1/sdtooling-api-be |
| Baseline version | `1.25.0` development line (last release in the upstream changelog: `1.24.0`, 2026-03-27) |
| Baseline commit | `744ede7809f1bb0af4c9ff04517fdbfdabbb32fa` (2026-04-17) |

## Modifications

| | |
|---|---|
| Modified by | EDNEL-RIOJA project team, for CNIE-ES |
| Public repository of this derivative work | https://github.com/cnie-es/simpl-sdtooling-api-be |
| Version of this derivative work | `1.25.0-edval` |
| Dates of modification | **2026-05-21 to 2026-09-17** |

The modifications are published under the same terms as the original work.

The complete source code of this derivative work is available at the public repository above as a
vetted release snapshot, and will remain freely available there for as long as the Work is
distributed. The upstream repository and exact baseline commit are recorded above. Each release
snapshot includes `SBOM.cyclonedx.json`, which binds the upstream and work revisions, release tag,
public repository, snapshot hash and image digest. The distribution history contains release
snapshots rather than a copy of the upstream Git history, so the complete set of changes is the
diff between the baseline commit `744ede78`, fetched from its authoritative upstream repository,
and the published snapshot. The tables below record what each file contributed, which also
satisfies the requirement of the Apache License to mark the files that were changed. The published
container images (`ghcr.io/cnie-es/sdtooling-api-be`) are built from that snapshot.

### Summary of the changes

- **Multilingual self-description schemas**: the single-language `ApplicationSchema`, `DataSchema`
  and `InfraSchema` were replaced by per-language `ApiSchema`, `CorpusSchema`, `LCRSchema` and
  `ModelSchema` in Catalan, English, Spanish, Basque and Galician, each in JSON and Turtle. This is
  the only change that removes files of the original work; see the note below the tables.
- **REST_API source** (template id 14): new source template and UI schema for both the APPLICATION
  and the DATA offer types, registered in `resourceAddress-config.yml` and exposed in the
  `sharingMethodId` allowable values. The sharing method was later moved from the APPLICATION to
  the DATA offer type.
- **AMAZON_S3 source with STS** (template id 15): new source template and UI schema; the existing
  `BULK_S3` template and schema were adjusted alongside it.
- **EDVAL property enrichment**: `EnrichServiceImpl`, `FederatedCatalogueServiceImpl`, `SDService`,
  `SDServiceImpl` and `ValidationServiceImpl` extended to enrich and validate the EDVAL properties.
- A `Makefile` with Maven test shortcuts, and a **GitHub Actions pipeline** that builds and
  publishes the container image, on tags or on demand.
- **Valencian self-description schemas** (2026-09-17): `ApiSchema_VA`, `CorpusSchema_VA`,
  `LCRSchema_VA` and `ModelSchema_VA`, replicated from the Catalan ones.

A second, non-functional group of changes (2026-09-10) adds the notices these licences require of a
derivative work: this file, the notice at the top of [README.md](README.md), the licence and
source-code metadata in `pom.xml` and in the OCI labels of the `Dockerfile`, and the reproduction of
the full official text of both licences inside [LICENSE](LICENSE), which previously only linked to
them. See [CHANGELOG.md](CHANGELOG.md) for the itemised list.

### Files added

| File | Date |
|---|---|
| `.github/workflows/build-image-on-tag.yml` | 2026-05-21, 2026-09-10 |
| `Makefile` | 2026-05-21 |
| `data/schemas/ApiSchema_CA.json` | 2026-06-04 |
| `data/schemas/ApiSchema_CA.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/ApiSchema_EN.json` | 2026-06-04 |
| `data/schemas/ApiSchema_ES.json` | 2026-06-04 |
| `data/schemas/ApiSchema_ES.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/ApiSchema_EU.json` | 2026-06-04 |
| `data/schemas/ApiSchema_EU.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/ApiSchema_GL.json` | 2026-06-04 |
| `data/schemas/ApiSchema_GL.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/ApiSchema_VA.json` | 2026-09-17 |
| `data/schemas/ApiSchema_VA.ttl` | 2026-09-17 |
| `data/schemas/CorpusSchema_CA.json` | 2026-06-04 |
| `data/schemas/CorpusSchema_CA.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/CorpusSchema_EN.json` | 2026-06-04 |
| `data/schemas/CorpusSchema_EN.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/CorpusSchema_ES.json` | 2026-06-04 |
| `data/schemas/CorpusSchema_ES.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/CorpusSchema_EU.json` | 2026-06-04 |
| `data/schemas/CorpusSchema_EU.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/CorpusSchema_GL.json` | 2026-06-04 |
| `data/schemas/CorpusSchema_GL.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/CorpusSchema_VA.json` | 2026-09-17 |
| `data/schemas/CorpusSchema_VA.ttl` | 2026-09-17 |
| `data/schemas/LCRSchema_CA.json` | 2026-06-04 |
| `data/schemas/LCRSchema_CA.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/LCRSchema_EN.json` | 2026-06-04 |
| `data/schemas/LCRSchema_EN.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/LCRSchema_ES.json` | 2026-06-04 |
| `data/schemas/LCRSchema_ES.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/LCRSchema_EU.json` | 2026-06-04 |
| `data/schemas/LCRSchema_EU.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/LCRSchema_GL.json` | 2026-06-04 |
| `data/schemas/LCRSchema_GL.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/LCRSchema_VA.json` | 2026-09-17 |
| `data/schemas/LCRSchema_VA.ttl` | 2026-09-17 |
| `data/schemas/ModelSchema_CA.json` | 2026-06-04 |
| `data/schemas/ModelSchema_CA.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/ModelSchema_EN.json` | 2026-06-04 |
| `data/schemas/ModelSchema_EN.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/ModelSchema_ES.json` | 2026-06-04 |
| `data/schemas/ModelSchema_ES.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/ModelSchema_EU.json` | 2026-06-04 |
| `data/schemas/ModelSchema_EU.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/ModelSchema_GL.json` | 2026-06-04 |
| `data/schemas/ModelSchema_GL.ttl` | 2026-06-04,2026-07-03 |
| `data/schemas/ModelSchema_VA.json` | 2026-09-17 |
| `data/schemas/ModelSchema_VA.ttl` | 2026-09-17 |
| `src/main/resources/resourceaddress/template/TEMPLATE_APPLICATION_REST_API_SOURCE_14.json` | 2026-05-21 |
| `src/main/resources/resourceaddress/template/TEMPLATE_DATA_AMAZON_S3_SOURCE_15.json` | 2026-06-15 |
| `src/main/resources/resourceaddress/template/TEMPLATE_DATA_REST_API_SOURCE_14.json` | 2026-05-21 |
| `src/main/resources/resourceaddress/ui-schema/UI_SCHEMA_APPLICATION_REST_API_SOURCE_14.json` | 2026-05-21 |
| `src/main/resources/resourceaddress/ui-schema/UI_SCHEMA_DATA_AMAZON_S3_SOURCE_15.json` | 2026-06-15 |
| `src/main/resources/resourceaddress/ui-schema/UI_SCHEMA_DATA_REST_API_SOURCE_14.json` | 2026-05-21 |
| `NOTICE.EDNEL.md` (this file) | 2026-09-10 |

### Files modified

| File | Date |
|---|---|
| `.gitignore` | 2026-07-03 |
| `CHANGELOG.md` | 2026-09-10 |
| `Dockerfile` | 2026-05-21, 2026-09-10 |
| `LICENSE` | 2026-09-10 |
| `README.md` | 2026-09-10 |
| `THIRD_PARTY_LICENCES.md` | 2026-07-03 |
| `charts/Chart.yaml` | 2026-09-10 |
| `charts/templates/deployment.yaml` | 2026-09-10 |
| `charts/values.yaml` | 2026-09-10 |
| `pipeline.variables.sh` | 2026-09-10 |
| `pom.xml` | 2026-09-10 |
| `scripts/docker/entrypoint.sh` | 2026-05-21 |
| `src/main/java/eu/europa/ec/simpl/sdtoolingbe/controller/v1/ResourceAddressController.java` | 2026-05-21 |
| `src/main/java/eu/europa/ec/simpl/sdtoolingbe/service/enrich/EnrichServiceImpl.java` | 2026-07-03 |
| `src/main/java/eu/europa/ec/simpl/sdtoolingbe/service/federatedcatalogue/FederatedCatalogueServiceImpl.java` | 2026-07-03 |
| `src/main/java/eu/europa/ec/simpl/sdtoolingbe/service/sd/SDService.java` | 2026-07-03 |
| `src/main/java/eu/europa/ec/simpl/sdtoolingbe/service/sd/SDServiceImpl.java` | 2026-07-03 |
| `src/main/java/eu/europa/ec/simpl/sdtoolingbe/service/validation/ValidationServiceImpl.java` | 2026-07-03 |
| `src/main/resources/resourceAddress-config.yml` | 2026-05-21,2026-06-15 |
| `src/main/resources/resourceaddress/template/TEMPLATE_DATA_BULK_S3_SOURCE_7.json` | 2026-06-15 |
| `src/main/resources/resourceaddress/ui-schema/UI_SCHEMA_DATA_BULK_S3_SOURCE_7.json` | 2026-06-15 |
| `src/test/java/eu/europa/ec/simpl/sdtoolingbe/service/resourceaddress/ResourceAddressServiceTest.java` | 2026-05-21 |

### Files renamed

| From | To | Date |
|---|---|---|
| `data/schemas/DataSchema.ttl` | `data/schemas/ApiSchema_EN.ttl` | 2026-06-04 |

### Files removed

| File | Date |
|---|---|
| `data/schemas/ApplicationSchema.json` | 2026-06-04 |
| `data/schemas/ApplicationSchema.ttl` | 2026-06-04 |
| `data/schemas/DataSchema.json` | 2026-06-04 |
| `data/schemas/InfraSchema.json` | 2026-06-04 |
| `data/schemas/InfraSchema.ttl` | 2026-06-04 |

No copyright, licence or disclaimer notice of the original work has been altered. Unlike the other
components of this fork, files of the original work **were** removed here: the five single-language
schema files listed above, superseded by the per-language schemas, plus one renamed. Nothing else
of the original work was removed.

The only change to [LICENSE](LICENSE) is the addition, below the original headings and credits line,
of the full official texts of the two licences that the file previously referenced only by
hyperlink; nothing in the original file was removed or reworded.

The per-file diff for every change is obtainable with:

```
git diff 744ede7809f1bb0af4c9ff04517fdbfdabbb32fa..ednel
```

For a published image, substitute the release tag it was built from for `ednel`.
