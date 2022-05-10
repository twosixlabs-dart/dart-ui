IMAGE_PREFIX = twosixlabsdart
IMAGE_NAME = dart-ui
IMAGE_NAME_UTIL = ontology-utils
IMG := $(IMAGE_PREFIX)/$(IMAGE_NAME)
IMG_UTIL := $(IMAGE_PREFIX)/$(IMAGE_NAME_UTIL)

ifndef GITHUB_REF_NAME
	APP_VERSION := "latest"
else ifeq ("$(GITHUB_REF_NAME)", "master")
	APP_VERSION := "latest"
else ifeq ("$(GITHUB_REF_TYPE)", "tag")
	APP_VERSION := $(shell cat version.sbt | cut -d\" -f2 | cut -d '-' -f1)
else
	APP_VERSION := $(GITHUB_REF_NAME)
endif

docker-login:
	docker login -u ${DOCKER_HUB_USERNAME} -p ${DOCKER_HUB_PASSWORD}

docker-build-app:
	SBT_OPTS="-Xmx4G -Xss2M" sbt clean prod:assembleApp
	docker build -t $(IMG):$(APP_VERSION) .

docker-push-app: docker-login docker-build
	docker push $(IMG):$(APP_VERSION)
	docker logout

docker-build-util:
	SBT_OPTS="-Xmx4G -Xss2M" sbt clean utilities/assembly
	docker build -f Dockerfile.utilities -t $(IMG_UTIL):$(APP_VERSION)
	docker logout

docker-push-util: docker-login docker-build-util
	docker push $(IMG_UTIL):$(APP_VERSION)
	docker logout

docker-build-all:
	SBT_OPTS="-Xmx4G -Xss2M" sbt clean utilities/assembly assembleApp
	docker build -t $(IMG):$(APP_VERSION) .
	docker build -f Dockerfile.utilities -t $(IMG_UTIL):$(APP_VERSION) .

docker-push-all: docker-login docker-build-all
	docker push $(IMG):$(APP_VERSION)
	docker push $(IMG_UTIL):$(APP_VERSION)
	docker logout
