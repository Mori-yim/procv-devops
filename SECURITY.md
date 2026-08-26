# SECURITY — ProCV

## Principes

- Aucun secret applicatif ne doit être versionné.
- `.env` est ignoré par Git ; `.env.example` ne contient que des valeurs fictives.
- Le backend s'exécute avec un utilisateur Linux non-root.
- MySQL n'est pas exposé sur le port hôte par défaut : seul le réseau Docker y accède.
- Les routes API protégées utilisent JWT.
- Les CV sont vérifiés côté serveur pour empêcher l'accès à ceux d'un autre utilisateur.
- Les images doivent être scannées avec Trivy avant publication.

## Audit des secrets

Avant un commit :

```bash
gitleaks detect --source . --redact
```

## Scan Trivy

Après construction :

```bash
trivy image --severity HIGH,CRITICAL --ignore-unfixed procv-backend:1.0.0
trivy image --severity HIGH,CRITICAL --ignore-unfixed procv-frontend:1.0.0
```

Pour obtenir le rapport complet :

```bash
trivy image --format table procv-backend:1.0.0 > trivy-backend.txt
trivy image --format table procv-frontend:1.0.0 > trivy-frontend.txt
```

## Politique de traitement

- `CRITICAL` : correction obligatoire avant publication, sauf justification explicite et acceptation du risque.
- `HIGH` : correction prioritaire ou documentation d'une impossibilité liée à une dépendance de base.
- `MEDIUM/LOW` : suivi dans le rapport de scan.

Les résultats dépendent de la date du scan et des versions d'images utilisées ; ils doivent donc être régénérés avant chaque release.
