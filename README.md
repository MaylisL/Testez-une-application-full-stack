# Yoga App

Ce projet est une application Angular / Java Spring Boot / MySQL destinée à la gestion de sessions de yoga.\
L’objectif est une stratégie de tests complète : unitaires, intégration et E2E, front et back.

## Commencer le projet
### Installation de la base de données

Créer la base de données:
```
mysql -u root -p
```
```
CREATE DATABASE yogaApp
```
Il faut également créer un USER qui a les accès à cette base de données. \
Les identifiants et mots de passe de ce user se trouvent dans le fichier `application.properties` dans `/back` \
Ces informations sont valables que dans l'environnement local

Exécuter le script d’initialisation qui est dans  `ressources/sql/script.sql` \
La base de données doit être sur le port par défaut de `MySQL 3306` 

Identifiants par défaut:\
`Admin`\
Email : yoga@studio.com\
Mot de passe : test!1234

### Installation de l’application

Suivre les readme déjà en place dans /front et dans /back

## Lancer les tests

### Front-end

#### Tests unitaires & d’intégration (Jest)
Il faut d'abord se placer dans:
```
cd front
```

Puis lancer les tests avec:
```
npm run test
```

Le rapport se trouve dans:
> front/coverage/lcov-report/index.html 

#### Tests End-to-End (Cypress)

Permet de lancer et build les tests:
```
npm run e2e
```

Permet d'ouvrir le mode interactif (UI):
```
npm run cy:open
```

Permet de lancer les tests:
```
npm run cy:run
```

Permet d'obtenir le coverage des tests e2e:
```
npm run e2e:coverage
```

### Back-end 

#### Tests unitaires & d’intégration (JUnit + Mockito)
Il faut se placer dans le dossier backend (/back):
```
mvn clean test
```

Le rapport coverage JaCoCo est disponible dans:
> back/target/site/jacoco/index.html

