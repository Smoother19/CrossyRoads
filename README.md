# CrossyRoads - Scala Project

**Auteurs :** Alan Ferilli & Noah Abraão  
**Cours :** Programmation (1ère année ISCB)

---

## 1. Description du jeu

Ce projet est une réimplémentation du célèbre jeu d'arcade **Crossy Road**, développée en langage Scala avec la librairie `FunGraphics`.

Le joueur incarne une poule qui doit traverser une série infinie de routes et de rivières générées aléatoirement. L'objectif est d'avancer le plus loin possible pour accumuler du score, tout en évitant de se faire écraser par des voitures ou de tomber dans l'eau. Le jeu intègre un système de défilement automatique (scrolling) qui force le joueur à rester en mouvement.

### Aperçu

**Menu Principal** 

<img width="350" height="500" alt="Menu du jeu" src="https://github.com/user-attachments/assets/6b40ccd1-2682-4857-97c7-694cff9a3af3" />



**En Jeu** 

<img width="350" height="500" alt="Gameplay" src="https://github.com/user-attachments/assets/685aad3e-83e3-4f5c-b628-deae53df8903" />

---

## 2. Mode d'emploi

### Installation et Lancement
1. **Cloner** ce dépôt (repository) directement depuis **IntelliJ IDEA**.
2. S'assurer que la librairie `hevs-graphics` est bien configurée dans le projet.
3. Ouvrir le fichier `Main.scala` situé à la racine des sources.
4. Exécuter le fichier (`Run 'Main'`) pour lancer le jeu.

### Contrôles
Le jeu se contrôle entièrement au clavier :

* **W** : Avancer (Gain de points)
* **S** : Reculer
* **A** : Aller à gauche
* **D** : Aller à droite
* **Enter** : Valider / Sélectionner dans le menu

PS: W et S permettent aussi de se déplacer dans le menu

### Règles du jeu
* **Score** : Chaque pas vers l'avant augmente votre score. Reculer le diminue.
* **Danger** : Si vous touchez une voiture ou tombez dans l'eau (sans être sur une bûche), la partie est finie.
* **Scrolling** : L'écran avance tout seul. Ne restez pas en bas de l'écran, le jeu vous forcera à avancer.

---

## 3. Structure du code

Le projet a été structuré de manière modulaire pour séparer la logique, l'affichage et les données.

### Architecture Principale
* **`Main.scala`** : Point d'entrée. Initialise la fenêtre graphique et lance la boucle de jeu principale qui synchronise l'affichage à 30 FPS.
* **`Game.scala`** : Le chef d'orchestre. Il gère les états du jeu (en cours, menu, game over), met à jour la logique (`update`) et dessine les éléments (`render`).
* **`GameConfig.scala`** : Un objet central regroupant toutes les constantes (taille de la fenêtre, vitesse des voitures, chemins des images). Cela permet de modifier l'équilibrage du jeu sans toucher au code logique.

### Gestion du Monde (Logique)
* **`WorldGrid.scala`** : C'est le cœur de la logique procédurale. Cette classe gère une grille 2D qui représente le terrain. Elle s'occupe de :
    * Générer les lignes (herbe, route, eau) de manière aléatoire mais jouable.
    * Faire bouger les obstacles (voitures et bûches) indépendamment.
    * Gérer le "scrolling" en décalant le tableau vers le bas et en créant une nouvelle ligne en haut.
* **`States.scala`** : Contient les définitions des types de cases (HERBE, ROUTE, EAU, etc.) pour éviter l'utilisation de "nombres magiques" dans le code.

### Entités et Inputs
* **`Player.scala`** : Gère la position (X, Y) du joueur et vérifie qu'il ne sort pas des limites gauche/droite de l'écran.
* **`Inputs.scala`** : Utilise un `KeyAdapter` pour écouter les touches (WASD). Il stocke l'état des touches pour que le jeu puisse les traiter au bon moment dans la boucle `update`.
* **`MusicManager.scala`** : Gère simplement le chargement et la lecture des sons (ambiance et Game Over).
* **`Menu.scala`** : Gère l'affichage de l'écran titre et la navigation simple avant le lancement de la partie.