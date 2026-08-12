# Task Manager — Frontend (React + Vite)

Interface web pour le backend Spring Boot `task-manager-backend`.

## Démarrage

```bash
npm install
npm run dev
```

L'application démarre sur `http://localhost:5173`. En développement, les appels vers `/api/...`
sont automatiquement relayés vers le backend (`http://localhost:8080`) via le proxy configuré
dans `vite.config.js` — aucune configuration CORS n'est nécessaire.

## Build de production

```bash
npm run build
npm run preview
```

En production, définissez `VITE_API_BASE_URL` (voir `.env.example`) avec l'URL complète de
l'API si le frontend n'est pas servi depuis le même domaine que le backend.

## Structure

```
src/
├── api/taskApi.js          # Client HTTP + normalisation des erreurs (ApiError)
├── components/              # Composants UI (carte tâche, formulaire, modale, filtre...)
├── hooks/useTasks.js        # Logique CRUD + filtrage par statut
├── utils/status.js          # Libellés de statut, formatage des dates
├── styles/index.css         # Design system (tokens couleur/typo, styles des composants)
├── App.jsx
└── main.jsx
```

## Fonctionnalités couvertes

- Créer une tâche (formulaire modal, validation client + serveur)
- Consulter la liste des tâches
- Consulter/modifier une tâche
- Supprimer une tâche (avec confirmation)
- Filtrer par statut (À faire / En cours / Terminée / Annulée)
- Affichage des erreurs de validation champ par champ (alignées sur les messages du backend)
- Gestion des erreurs réseau/serveur avec bandeau + action « Réessayer »
