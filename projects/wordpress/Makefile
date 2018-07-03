GIT_ROOT:=$(shell git rev-parse --show-toplevel)
include $(GIT_ROOT)/platform/makefiles/common.mak

.EXPORT_ALL_VARIABLES:

build: prebuild
	@$(DOCKER) build -f $(DOCKER_FILE) -t $(IMAGE_NAME):$(IMAGE_VERSION_FIXED) $(DOCKER_FOLDER)

push:
	@$(DOCKER) push $(IMAGE_NAME)

include $(DOCKER_SOURCE)
.PHONY: $(DOCKER_PREBUILD)
prebuild: $(DOCKER_PREBUILD)
