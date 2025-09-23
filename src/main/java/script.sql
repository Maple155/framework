CREATE DATABASE prevision;

USE prevision; 

CREATE TABLE personne (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR (255) NOT NULL,
    pwd VARCHAR (255) NOT NULL
);

CREATE TABLE prevision (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR (255) NOT NULL,
    montant INTEGER
);

CREATE TABLE depense (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    id_prevision INTEGER,
    montant INTEGER
);

INSERT INTO personne (nom) VALUES ('Ranto');