## DIFF JBoss 6.4.0 PBdoc REV6 - 10/11/2020

#### Instalação do DIFF sobre uma versão limpa do JBoss EAP 6.4.0

- IMPORTANTE: considerar as seguintes variáveis de ambiente para todo o processo:**
  - `$JBOSS_HOME`: diretório-base da instalação do JBoss 6.4.0;
  - `$PBDOC_SOURCE`: diretório-base do código-fonte do PBdoc;

- Efetuar o download do JBoss EAP 6.4.0 limpo [aqui](https://developers.redhat.com/download-manager/file/jboss-eap-6.4.0.GA.zip);
- Copiar as pastas para `$JBOSS_HOME`, mesclando os arquivos de mesmo diretório abaixo:
  - `$PBDOC_SOURCE/docker/modules/`
  - `$PBDOC_SOURCE/docker/standalone/`
  - `$PBDOC_SOURCE/docker/welcome-content/`
- Executar o patch `patch.sh` disponível na pasta copiada em `$JBOSS_HOME/standalone/configuration`, **abrindo o terminal nesta pasta** e executando:

```sh
$ ./patch.sh
```

#### Executando instância do PostgreSQL 9.6 (versão máxima suportada pelo JBoss EAP 6.4.0)

```sh
$ docker run --name pbdoc-postgresql-9.6 -p 5433:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:9.6
```

O comando acima executa uma instância do PostgreSQL 9.6 no Docker, expondo a porta `5433` e utilizando os seguintes dados de acesso para usuário administrador:
- Usuário: `postgres`
- Senha: `postgres`

A instância fica acessível na máquina host com os seguintes dados:

- IP: `172.17.0.1`
- Porta: `5433`
