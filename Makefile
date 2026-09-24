JAVA_HOME ?= /usr/lib/jvm/java-21-openjdk-amd64
MVN       := JAVA_HOME=$(JAVA_HOME) mvn

.PHONY: test test-controllers test-services test-report clean help

## Run all unit tests
test:
	$(MVN) test

## Run only controller tests
test-controllers:
	$(MVN) test -Dtest="**/controller/**/*Test"

## Run only service tests
test-services:
	$(MVN) test -Dtest="**/service/**/*Test"

## Run a specific test class  (e.g. make test-class CLASS=ResourceAddressServiceTest)
test-class:
	$(MVN) test -Dtest=$(CLASS)

## Generate Surefire HTML report (opens in target/site/surefire-report.html)
test-report:
	$(MVN) surefire-report:report-only

## Clean build artifacts
clean:
	$(MVN) clean

help:
	@grep -E '^##' Makefile | sed 's/## //'
