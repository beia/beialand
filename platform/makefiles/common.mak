# General variables
GIT_ROOT:=$(shell git rev-parse --show-toplevel)
GIT_REF:=$(shell git symbolic-ref --short HEAD)
ROOT=$(GIT_ROOT)

# General settings
DOCKER_SOURCE:=source.docker
STACK_SOURCE:=source.stack

# Constants
INVALID_CHARS:=/

# Settings for building docker images
DOCKER_FOLDER?=.
DOCKER_FILE?=Dockerfile
DOCKER?=docker
ifeq (master,${GIT_REF})
IMAGE_VERSION=latest
else
IMAGE_VERSION=${GIT_REF}
endif

IMAGE_VERSION_FIXED=$(subst $(INVALID_CHARS),_,$(IMAGE_VERSION))

# Settings for deploying stacks
STACK_FILE?=stack.yml
