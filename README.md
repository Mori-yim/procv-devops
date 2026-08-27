
# ProCV — Stack full-stack dockerisée

ProCV est une application de création de CV professionnels destinée au marché de l'emploi camerounais. Cette version transforme le projet en **stack reproductible de bout en bout** : frontend React, API Spring Boot, MySQL, Docker Compose, healthchecks, réseau Docker interne, volume persistant et images multi-stage.

> **Objectif DevOps :** après création de `.env`, un nouveau développeur peut cloner le dépôt et lancer toute la stack avec une seule commande.

## Architecture

```text
                    Navigateur
                        |
                     :4200
                        |
                +-------v--------+
                | React + Nginx  |
                | SPA / API proxy |
                +-------+--------+
                        |
                 Docker network
                        |
                  :8080 interne
                        |
                +-------v--------+
                | Spring Boot API|
                | JWT + JPA + PDF|
                +-------+--------+
                        |
                    :3306 interne
                        |
                +-------v--------+
                |   MySQL 8.4    |
                | named volume    |
                +-----------------+
```

### Services

| Service | Technologie | Port hôte | Rôle |
|---|---|---:|---|
| `frontend` | React + Vite + Nginx | 4200 | Interface web et reverse proxy `/api` |
| `backend` | Spring Boot 3.3 + Java 17 | 8080 | API, JWT, CV, PDF |
| `db` | MySQL 8.4 | non exposé | Persistance des données |

Le réseau Docker est créé automatiquement par Compose. Les conteneurs utilisent les **noms de services** (`backend`, `db`) et aucune IP n'est codée en dur.

## Prérequis

- Windows 11, Linux ou macOS
- Docker Desktop récent avec Docker Compose v2
- Git
- Compte Docker Hub uniquement si tu souhaites publier les images

Vérification :

```bash
docker --version
docker compose version
```

## Installation — démarrage en une commande

### 1. Cloner

```bash
git clone <URL_DU_DEPOT>
cd procv
```

### 2. Créer l'environnement

PowerShell :

```powershell
Copy-Item .env.example .env
```

Linux/macOS :

```bash
cp .env.example .env
```

Modifie au minimum :

```env
MYSQL_PASSWORD=un_mot_de_passe_fort
MYSQL_ROOT_PASSWORD=un_autre_mot_de_passe_fort
JWT_SECRET=une_cle_aleatoire_d_au_moins_32_caracteres
```

### 3. Lancer toute la stack

```bash
docker compose up --build -d
```

C'est la commande de démarrage complète.

### 4. Vérifier

```bash
docker compose ps
docker compose logs -f backend
```

Le backend doit passer à l'état `healthy` avant le frontend grâce à `depends_on` + healthchecks.

Application : **http://localhost:4200**

API directe : **http://localhost:8080/api**

Healthcheck : **http://localhost:8080/actuator/health**

## Arrêter sans perdre les données

```bash
docker compose down
```

Le volume nommé `procv_mysql_data` est conservé.

Pour supprimer aussi la base :

```bash
docker compose down -v
```

## Fonctionnalités applicatives

- inscription et connexion JWT ;
- création, modification et consultation de CV ;
- contrôle serveur de propriété des CV ;
- limite d'un CV pour le plan FREE ;
- passage Premium simulé ;
- génération PDF côté serveur ;
- filigrane pour le plan FREE ;
- interface React responsive ;
- reverse proxy Nginx et fallback SPA `try_files`.

## Structure

```text
procv/
├── frontend/
│   ├── src/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── vite.config.js
├── backend/
│   ├── src/main/java/cm/procv/
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
├── .env.example
├── .gitignore
├── SECURITY.md
└── README.md
```

## Optimisation Docker

### Backend

Dockerfile multi-stage : Maven/JDK uniquement pendant le build, puis JRE Alpine pour l'image finale.

**Budget : < 300 Mo.** Vérification :

```bash
docker image inspect local/procv-backend:1.0.0 --format '{{.Size}}'
```

### Frontend

Dockerfile multi-stage : Node uniquement pour compiler, Nginx Alpine pour servir les fichiers statiques.

**Budget : < 50 Mo.** Vérification :

```bash
docker image inspect local/procv-frontend:1.0.0 --format '{{.Size}}'
```

Afficher les tailles lisibles :

```bash
docker images '*procv*'
```

> Les tailles exactes doivent être relevées sur la machine de build finale ; elles ne sont pas inventées dans la documentation.

## Sécurité

Lire [SECURITY.md](SECURITY.md).

Audit secrets :

```bash
gitleaks detect --source . --redact
```

Scan :

```bash
trivy image --severity HIGH,CRITICAL --ignore-unfixed local/procv-backend:1.0.0
trivy image --severity HIGH,CRITICAL --ignore-unfixed local/procv-frontend:1.0.0
```

## Docker Hub — release versionnée

Créer et tester le compte Docker Hub, puis :

```bash
docker login
```

Renseigner d'abord dans `.env` :

```env
DOCKERHUB_USERNAME=MON_USER
IMAGE_TAG=1.0.0
```

Construire :

```bash
docker compose build
```

Les images sont alors nommées :

```text
MON_USER/procv-backend:1.0.0
MON_USER/procv-frontend:1.0.0
```

Puis publier :

```bash
docker push MON_USER/procv-backend:1.0.0
docker push MON_USER/procv-frontend:1.0.0
```

Dans `.env`, renseigne :

```env
DOCKERHUB_USERNAME=MON_USER
IMAGE_TAG=1.0.0
```

> Ne publie pas uniquement `latest`. Le tag `1.0.0` permet un rollback précis.

## Test à froid / reproductibilité

Sur une machine propre ou un nouveau dossier :

```bash
git clone <URL_DU_DEPOT> procv-cold
cd procv-cold
cp .env.example .env
# modifier les secrets

docker compose up --build -d
docker compose ps
```

Pour simuler un environnement local plus propre :

```bash
docker compose down -v --remove-orphans
docker image rm local/procv-backend:1.0.0 local/procv-frontend:1.0.0 2>/dev/null || true
docker compose up --build -d
```

## Dépannage

### Page blanche

```bash
docker compose logs frontend
curl http://localhost:4200
```

Vérifier aussi la console du navigateur. Nginx utilise :

```nginx
try_files $uri $uri/ /index.html;
```

### Backend non healthy

```bash
docker compose logs backend
docker compose logs db
```

### Base non prête

```bash
docker compose ps
docker inspect procv-db --format '{{json .State.Health}}'
```

### Réinitialiser complètement la base

```bash
docker compose down -v
```

## Limites et améliorations futures

- remplacer le paiement simulé par les APIs officielles MTN MoMo / Orange Money et des webhooks ;
- ajouter migrations Flyway/Liquibase à la place de `ddl-auto: update` ;
- ajouter tests unitaires et tests d'intégration ;
- ajouter observabilité complète (metrics/logs/traces) ;
- renforcer les CSP/headers Nginx et la rotation des clés JWT ;
- ajouter CI/CD avec build, Trivy, Gitleaks et publication Docker Hub ;
- ajouter des tests E2E frontend.

