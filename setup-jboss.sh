#/bin/bash 

# merge jboss base and diff provided by codata
unzip jboss-eap-6.4.0.zip
mkdir jboss-6.4.0-diff.rev6
tar -xf jboss-6.4.0-diff.rev6.tar.xz -C jboss-6.4.0-diff.rev6
cp -a jboss-6.4.0-diff.rev6/. jboss-eap-6.4
cd jboss-eap-6.4/standalone/configuration
./patch.sh

# optional: change output folder
# PBDOC_OUTPUT_FOLDER="$(pwd)/pbdoc/documentos"
# sed -i '313d' standalone.xml
# sed -i '313 i \        <property name="pbdoc.documento.armazenamento" value='\""$PBDOC_OUTPUT_FOLDER"\"'/>' standalone.xml