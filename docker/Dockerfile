FROM daggerok/jboss-eap-6.4:6.4.22-alpine

USER root

RUN apk add --no-cache fontconfig ttf-dejavu
RUN fc-cache -f
RUN ln -s /usr/lib/libfontconfig.so.1 /usr/lib/libfontconfig.so && \
    ln -s /lib/libuuid.so.1 /usr/lib/libuuid.so.1 && \
    ln -s /lib/libc.musl-x86_64.so.1 /usr/lib/libc.musl-x86_64.so.1
ENV LD_LIBRARY_PATH /usr/lib

USER ${JBOSS_USER}

RUN chmod -R 777 $JBOSS_HOME

# Diretório-base para arquivos utilizados pela aplicação
ENV PBDOC_HOME /home/jboss/pbdoc
RUN mkdir -p $PBDOC_HOME
RUN chmod -R 777 $PBDOC_HOME

ENV DEPLOYMENTS_HOME $JBOSS_HOME/standalone/deployments

RUN mkdir -p $JBOSS_HOME/welcome-content/ckeditor
COPY /welcome-content/ckeditor $JBOSS_HOME/welcome-content/ckeditor/
COPY /modules $JBOSS_HOME/modules/
COPY /target/*.war $DEPLOYMENTS_HOME/

RUN mkdir -p $JBOSS_HOME/standalone/configuration/siga
COPY /standalone/configuration/siga $JBOSS_HOME/standalone/configuration/siga/

RUN mv $JBOSS_HOME/standalone/configuration/standalone.xml $PBDOC_HOME/standalone.xml.original

COPY /standalone.xml $PBDOC_HOME

# OPENSHIFT (config maps do standalone.xml com volume montado em $PBDOC_SCRIPTS_HOME)
RUN ln -s $PBDOC_HOME/standalone.xml $JBOSS_HOME/standalone/configuration/standalone.xml
