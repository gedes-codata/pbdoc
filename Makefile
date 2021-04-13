CWD = $(dir $(realpath $(firstword $(MAKEFILE_LIST))))
USER_ID = $(shell id -u)

.PHONY: package build rebuild

package: VERSAO ?= develop
package:
	docker run -it --rm \
		-v $(CWD):/opt/src/ \
		-v $(HOME)/.m2:/var/maven/.m2 \
		-e MAVEN_CONFIG=/var/maven/.m2 \
		--workdir /opt/src \
		--user $(USER_ID) \
		maven:3.6.3-openjdk-8-slim mvn clean package -U \
			-Pbuild-siga \
			-Duser.home=/var/maven \
			-Dmaven.test.skip \
			-Dsiga.versao=$(VERSAO)

build: VERSAO ?= latest
build:
	if [ -d docker/target ]; then rm -r docker/target; fi
	cp -r target docker/target
	docker build -t siga-doc:$(VERSAO) ./docker

rebuild: package build

start-containers:
	docker start bluc.server db.server viz.server

stop-containers:
	docker stop bluc.server db.server viz.server

