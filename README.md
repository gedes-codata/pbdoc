# SIGA - Sistema de Gestão Administrativa

### Módulos

- SIGA-DOC: Gestão Documental
- SIGA-WF: Workflow
- SIGA-GI: Gestão de Identidade
- SIGA-GC: Gestão de Conhecimento
- SIGA-SR: Serviços e Tickets
- SIGA-TP: Solicitação de transportes

### Ambiente de Desenvolvimento Homologado

- [JDK 8](https://adoptopenjdk.net/releases.html?variant=openjdk8&jvmVariant=hotspot)
- [Eclipse 2020.03](https://www.eclipse.org/downloads/packages/release/2020-03/r/eclipse-ide-enterprise-java-developers-includes-incubating-components)
- [Red Hat JBoss EAP 6.4.0](https://developers.redhat.com/download-manager/file/jboss-eap-6.4.0.GA.zip)
- [Docker CE](https://docs.docker.com/engine/install)
- [Maven 3.5.4](https://archive.apache.org/dist/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.tar.gz)

### Configuração

#### 1. Iniciar Infraestrutura Básica

Os comandos abaixo configuram três containers:
* Blue Crystal Server: para o serviço de certificação digital
* Database Server: para hospedar um banco de dados PostgreSQL
* GraphViz Server: para o serviço de geração de gráficos

```sh
# Blue Crystal Server (bluc.server)
$ docker run -d --name bluc.server -h bluc.server -p 50010:8080 siga/bluc.server

# PostgreSQL 9.6 (db.server)
$ docker run -d --name db.server -h db.server -p 5433:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:9.6

# GraphViz (viz.server)
$ docker run -d --name viz.server -h viz.server -p 49165:8080 omerio/graphviz-server 8080

```

> ATENÇÃO: se o seu Docker for executado como superusuário, os comandos acima devem ser precedidos pelo comando `sudo`.

Em um segundo momento, quando os containers já estiverem criados, basta executar o comando abaixo na raiz do projeto para dar start em todos:

```
make start-containers postgres
```

Note que há o equivalente para parar os processos dos containers: `make stop-containers`

#### 2. Configurar o Banco de Dados PostgreSQL

1. Requisitar link para download do arquivo dump
2. Aplicar dump, substituindo 'filename' pelo nome do arquivo do dump

```sh
# Copy dump file:
docker cp  <filename>.dump db.server:/

# Access db.server container
docker exec -it db.server bash

# Access postgres
psql -U postgres

# create database
create database pbdoc

# Apply dump
pg_restore --username postgres --dbname pbdoc <filename>.dump
```
    
3. Para adicionar todos os schemas ao `search_path` do usuário, no PostgreSQL, execute o comando:

```sql
ALTER ROLE postgres SET search_path TO "corporativo", "siga", "sigagc", "sigapmp", "sigasr", "sigatp", "sigawf", "public";
```

#### 3. Configurar dependências do projeto

1. Clonar repositório base do projeto [SIGA-DOC](https://gitcodata.pb.gov.br/siga-doc/siga-doc)

2. Configurar acesso à VPN da CODATA 
   
- Algumas dependências deste projeto estão presentes em repositórios privados da CODATA, providos pelo servidor de repositórios.
  Solicitar credenciais via: https://codata.pb.gov.br/atendimentos. A equipe de infra vai enviar um link para download da chave no seu email
- [Setup VPN no Ubuntu](https://askubuntu.com/questions/187511/how-can-i-use-a-ovpn-file-with-network-manager)

3. Modificar as configurações do maven para incluir o servidor de repositórios da CODATA. 
   Para isso, edite o arquivo settings.xml (geralmente, localizado ~/.m2), [adicionando um novo mirror](https://gitcodata.pb.gov.br/gedes/maven-ci/blob/master/settings.xml):
   
```xml
<mirrors>
   <mirror>
    <id>codata</id>
    <name>CODATA Public Repository</name>
    <url>http://nexus.paas.pb.gov.br/repository/maven-public</url>
    <mirrorOf>*</mirrorOf>
   </mirror>
</mirrors>
```

4. Compilar/Build o projeto via maven. Execute o comando abaixo na raiz do projeto.

```sh
mvn clean package
```

#### 4. Configurar o JBoss EAP

1. Fazer download do [_DIFF_ do JBoss EAP de Desenvolvimento](https://docs.pb.gov.br/s/iDD1iFb9ZUsGROI);
2. Executar o script abaixo (localizado na raiz do projeto) para mesclar o conteúdo do diff com o do [Red Hat JBoss EAP 6.4.0](https://developers.redhat.com/download-manager/file/jboss-eap-6.4.0.GA.zip):
   
```sh
./setup-jboss.sh
```

* OBS: O script acima assume que os arquivos do diff e JBoss EAP 6.4.0 estão no mesmo diretório
* OBS 2: Por padrão, o arquivo `jboss-eap-6.4/standalone/configuration/standalone.xml` vem configurado com o 
  despejo de arquivos, conforme property abaixo. Para mudar o local de despejo, basta alterar o valor da property.
  
```xml
<property name="pbdoc.documento.armazenamento" value="/opt/pbdoc/documentos"/>
```


3. Criar uma variável de ambiente `$JBOSS_HOME`, apontando para o diretório resultante do passo 2.

### Configuração do projeto no Eclipse e execução no JBoss de desenvolvimento

1. Importar projeto:

`File -> Import... -> Maven -> Existing Maven Projects`

* OBS: a o processo de indexação do eclipse pode demorar um pouco. 
  Talvez seja preciso realizar Refresh após cada build via maven.

2. Adicionar conector JBoss EAP (versão 6.1+) no Eclipse, apontando o 'home directory' para o 
diretório base do servidor (`$JBOSS_HOME`);
  
`Windows -> Preferences -> Server -> Runtime Environments -> Add.. -> Red Hat JBoss Middleware`

* OBS: configure a Runtime JRE para a versão a JDK 1.8

3. Desativar validadores desnecessários no Eclipse via preferences. 
   A figura abaixo ilustra como deve ser a configuração final

![https://imgur.com/3tHhVp2](https://i.imgur.com/3tHhVp2.png)

4. Adicionar JBoss EAP Server. 

- Na aba Servers, clicar com botão direito para criar um novo Server;
- Escolha a versão: Red Hat JBoss Enterpreise Application Plataform 6.1+;
- Na caixa de seleção, escolha a runtime criada no passo anterior.
- Por fim, adicione todos os recursos disponíveis. 

  
5. Configurações de deployment do JBoss EAP no Eclipse

- Na aba Server, clique duplo no servidor criado no passo 4.
- A figura abaixo ilustra como deve ser a configuração: deploy ZIP para o **siga-vraptor-module**

![https://imgur.com/e8AZvFh](https://i.imgur.com/e8AZvFh.png)


6. Com os projetos importados na IDE Eclipse, clicar com o botão direito do mouse sobre o projeto **siga** e seguir no comando **Debug As > Debug on Server**

# [Para saber mais sobre o SIGA-DOC](https://github.com/projeto-siga/siga/wiki/sobre-o-siga-doc)

- [Manual do Usuário](https://sway.com/6tcLGC0jYE7zUSBX), uma contribuição do Governo do Estado de São Paulo!
- [Focumentação para o desenvolvedor - Javadoc](http://projeto-SIGA.github.io/artifacts/javadoc/).
- [Instalar uma versão de testes do SIGA-DOC, utilizando o Docker](https://github.com/projeto-SIGA/docker).
- [Para tirar dúvidas, entre em contato através do forum](https://groups.google.com/forum/#!forum/SIGA-DOC).
