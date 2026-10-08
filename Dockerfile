FROM eclipse-temurin:21-jre@sha256:49e21e16e3c86eb7816a44a67549910ed090fbeb40c29c525d58bf5e02e91b0f

ARG IMAGE_REVISION="0000000000000000000000000000000000000000"
ARG IMAGE_CREATED="1970-01-01T00:00:00Z"

# EUPL-1.2 (Art. 5) and Apache License 2.0 (Section 4): this image ships a modified version of SIMPL
# sdtooling-api-be. Both licences, the third-party notices and the modification notice travel with
# the image, and the labels below point to the repository where the source code is available.
LABEL org.opencontainers.image.title="sdtooling-api-be (CNIE-ES fork)" \
      org.opencontainers.image.description="Modified version of SIMPL sdtooling-api-be (upstream commit 744ede78), modified by the EDNEL-RIOJA project team for CNIE-ES between 2026-05-21 and 2026-09-17. See /licenses/NOTICE.EDNEL.md." \
      org.opencontainers.image.version="1.25.0-edval" \
      org.opencontainers.image.vendor="CNIE-ES" \
      org.opencontainers.image.licenses="Apache-2.0 AND EUPL-1.2" \
      org.opencontainers.image.source="https://github.com/cnie-es/simpl-sdtooling-api-be" \
      org.opencontainers.image.revision="${IMAGE_REVISION}" \
      org.opencontainers.image.created="${IMAGE_CREATED}"

# Create a non-root user and group with UID/GID 1001 to match PVC ownership
RUN groupadd -g 1001 simplgroup && useradd -u 1001 -g simplgroup -m simpluser

WORKDIR /home/simpluser

# The release/build.sh hook produces the artifact; the image only copies it.
COPY --chown=simpluser:simplgroup target/sdtooling-api-be.jar app.jar
COPY --chown=simpluser:simplgroup pipeline.variables.sh .
COPY --chown=simpluser:simplgroup scripts/docker/entrypoint.sh .
COPY --chown=simpluser:simplgroup data/shapes ./tmp/shapes/
COPY --chown=simpluser:simplgroup data/schemas ./tmp/schemas/

# The notices must travel with every copy of the Work (EUPL-1.2 Art. 5, Apache License 2.0 §4).
COPY LICENSE NOTICE NOTICE.json NOTICE.EDNEL.md THIRD_PARTY_LICENCES.md THIRD_PARTY_LICENCES.xml /licenses/

# Grant execute permissions and prepare data directory
RUN mkdir -p /home/simpluser/data
RUN chown -R simpluser:simplgroup /home/simpluser
RUN chmod -R 770 /home/simpluser
RUN chmod +x /home/simpluser/entrypoint.sh

# Set environment and default user
USER simpluser

# Start entrypoint script and run the application
ENTRYPOINT ["/bin/sh", "-c", "/home/simpluser/entrypoint.sh && exec java -jar /home/simpluser/app.jar"]
