IMAGE_PREFIX = docker.causeex.com/dart/
IMAGE_NAME = dart-ui-consolidated
IMG := $(IMAGE_PREFIX)$(IMAGE_NAME)

ifndef CI_COMMIT_REF_NAME
	APP_VERSION := "latest"
else ifeq ("$(CI_COMMIT_REF_NAME)", "master")
	APP_VERSION := "latest"
else
	APP_VERSION := $(shell cat version.sbt | cut -d\" -f2 | cut -d '-' -f1)
endif

docker-build:
	SBT_OPTS="-Xmx4G -Xss2M" sbt clean dev:assembleApp
	docker build -t $(IMG):$(APP_VERSION) .

docker-push: docker-build
	docker push $(IMG):$(APP_VERSION)

docker-build-dev:
	SBT_OPTS="-Xmx4G -Xss2M" sbt dev:assembleApp
	docker build -t $(IMG)/dev-version:latest .

docker-push-dev: docker-build-dev
	docker push $(IMG)/dev-version:latest

clean:
	docker images | grep $(IMAGE_NAME) | grep -v IMAGE | awk '{print $3}' | docker rmi -f
