# Projet Flightmaster

Flightmaster est un projet de gestion du trafic aérien qui utilise des algorithmes de coloration de graphes pour optimiser et minimiser les retards.


## Structure du Projet

Le projet est organisé en modules Maven, comprenant :

- **flightmaster-app** : Module principal de l'application Flightmaster qui intègre l'interface utilisateur pour la gestion du trafic aérien.

- **flightmaster-lib** : Bibliothèque de fonctionnalités utilisée par l'application, incluant notamment les algorithmes de coloration de graphes.

## Build le Projet

Pour construire le projet, assurez-vous d'avoir Maven installé, puis exécutez la commande suivante à la racine du projet :

```bash
mvn clean install
```

Pour obtenir le fichier JAR de l'application Flightmaster, naviguez vers `flightmaster-app/target/`. Le fichier JAR correspondant est `flightmaster-app-1.0.0.jar`.

## Documentation Java (Javadoc)

La documentation Java est automatiquement mise à jour à chaque commit et est accessible via le lien suivant :

[http://p2306713.pages.univ-lyon1.fr/flightmaster](http://p2306713.pages.univ-lyon1.fr/flightmaster/)

## Exécution des Tests

Avant chaque build, les tests sont automatiquement exécutés pour vérifier la validité du code. Pour lancer uniqement les tests, utilisez la commande suivante :

```bash
mvn clean test
```