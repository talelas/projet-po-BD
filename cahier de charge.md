Mini-Projet en POO-Base de données 
Système de Gestion d’une Pharmacie 

Description du Sujet 
Le projet consiste à concevoir et implémenter un système de gestion d’une 
pharmacie. 
Le système doit permettre une gestion complète, intuitive et automatisée des 
opérations principales : 
• Gestion des stocks de médicaments  
• Gestion des fournisseurs et des clients 
• Suivi des opérations commerciales (ventes et commandes) 
Fonctionnalités attendues 
Les employés de la pharmacie sont les seuls utilisateurs du système, qui doivent y 
accéder grâce à un login/mot de passe. Ils doivent pouvoir : 
• Ajouter, modifier et supprimer des produits. 
• Gérer les commandes fournisseurs (création, modification, annulation, 
réception). 
• Enregistrer les ventes clients. 
• Mettre à jour automatiquement les stocks après chaque vente ou réception de 
commande. 
• Recevoir des alertes lorsque certains produits atteignent un seuil minimal de 
stock. 
• Consulter l’historique des achats clients. 
Page 1/3 
• Permettre à l’administrateur de générer des rapports d’analyse 
‣ État des stocks 
‣ Chiffre d’affaires 
‣ Performance des fournisseurs 
Contraintes techniques 
L’application doit respecter les contraintes techniques suivantes : 
‣ Le développement doit être réalisé avec le langage 
Java
. 
‣ Une interface graphique doit être développée en Java, en utilisant la librairie de 
votre choix (Swing, JavaFX, etc.) 
‣ Le code source doit respecter les contraintes suivantes: 
• Le projet doit contenir un ou plusieurs packages 
• Noms des packages : uniquement en minuscules. 
• Noms des classes et interfaces: commencent par une majuscule (ex.: 
Produit, GestionStock). 
• Noms des méthodes, attributs et variables : commencent par une minuscule 
et utilisent le camelCase (ex. : mettreAJourStock(), quantiteDisponible). 
• Visibilité des attributs : 
‣ Par défaut, les attributs doivent être privés (private). 
‣ Les accès externes doivent passer par des getters/setters, uniquement si 
cela est utile. 
• Constructeurs : ne les définir que lorsqu’ils sont nécessaires. 
• Commentaires : - Commentez les méthodes complexes pour expliquer leur rôle. - Restez concis et pertinents : ne commentez pas l’évidence. 
‣ Exceptions : Créez au moins trois exceptions personnalisées.  
• La base de données doit être structurée, normalisée (toutes les tables doivent 
respecter 3FN) et développée sur le SGBDR 
MySQL
. 
• Une connexion sécurisée entre l’application Java et MySQL doit être établie. Le 
contrôle d’accès par login/mot de passe doit être réalisé côté applicatif, mais 
également du côté de la base de données (création d’utilisateurs autorisés dans la 
base de données). 
Page 2/3 
Livrables 
Les étudiants devront remettre un dossier numérique unique comportant: 
• Le
 
code
 
source
 
complet
 de l’application. 
• Le
 
script
 
SQL
 pour la création de la base de données. 
•Un
 
rapport
 
unique
 (au maximum 6 pages) comportant: 
‣
Le diagramme de classe avec la description des classes. 
‣
Les principales fonctionnalités implémentées 
‣
Le Modèle Conceptuel des Données (MCD ou modèle entité- association) 
‣
Le Modèle Logique des Données (MLD) 
‣
Les difficultés rencontrées dans le projet. 
Évaluation 
Pour la partie POO 
L’évaluation portera sur les critères suivants: 
• Qualité de la conception et de l’implémentation des fonctionnalités 
• Respect des principes de la Programmation Orientée Objet 
• Modularité et extensibilité du code (facilité d’évolution du projet) 
Pour la partie BD 
L’évaluation portera sur les critères suivants: 
• Cohérence entre MCD et MLD 
• Qualité de conception de la base et respect de la normalisation 
• Fluidité de la connexion entre l’application et la base 
• Pertinence des requêtes SQL et des structures créées