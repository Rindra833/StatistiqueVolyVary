CREATE DATABASE voly_vary;

\c voly_vary

CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(100),
    nom VARCHAR(100),
    prenom VARCHAR(100),
    telephone VARCHAR(50),
    date DATE
);

CREATE TABLE type_transaction (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100)
);

CREATE TABLE statut_transaction (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100),
    sigle VARCHAR(20)
);

CREATE TABLE categorie_fourniture (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100)
);

CREATE TABLE fourniture (
    id SERIAL PRIMARY KEY,
    id_categorie INT,
    reference VARCHAR(100),
    prix_unitaire DECIMAL(10,2),
    FOREIGN KEY (id_categorie) REFERENCES categorie_fourniture(id)
);

CREATE TABLE transaction(
    id SERIAL PRIMARY KEY,
    id_client INT,
    id_type INT,
    reference VARCHAR(100),
    date TIMESTAMP,
    montant_total DECIMAL(10,2),
    FOREIGN KEY (id_client) REFERENCES client(id),
    FOREIGN KEY (id_type) REFERENCES type_transaction(id)
);

CREATE TABLE detail_transaction (
    id SERIAL PRIMARY KEY,
    id_transaction INT,
    id_fourniture INT,
    quantite INT,
    montant_ligne DECIMAL(10,2),
    FOREIGN KEY (id_transaction) REFERENCES transaction(id),
    FOREIGN KEY (id_fourniture) REFERENCES fourniture(id)
);

CREATE TABLE historique_transaction (
    id SERIAL PRIMARY KEY,
    id_transaction INT,
    id_statut INT,
    date TIMESTAMP,
    FOREIGN KEY (id_transaction) REFERENCES transaction(id),
    FOREIGN KEY (id_statut) REFERENCES statut_transaction(id)
);




-- collecte
CREATE TABLE statut_lot_paddy (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255),
    sigle VARCHAR(50)
);

CREATE TABLE collecte (
    id SERIAL PRIMARY KEY,
    prix_unitaire DECIMAL(10, 2)
);

CREATE TABLE lot_paddy (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(100),
    quantite DECIMAL(12, 2),
    taux_humidite DECIMAL(5, 2),
    date DATE,
    prix_collecte DECIMAL(12, 2),
    id_collecte INT,
    FOREIGN KEY (id_collecte) REFERENCES collecte(id)
);

CREATE TABLE historique_collecte (
    id SERIAL PRIMARY KEY,
    id_client INT,
    id_lot_paddy INT,
    id_statut INT,
    date DATE,
    FOREIGN KEY (id_client) REFERENCES client(id),
    FOREIGN KEY (id_lot_paddy) REFERENCES lot_paddy(id),
    FOREIGN KEY (id_statut) REFERENCES statut_lot_paddy(id)
);

CREATE TABLE reduction (
    id SERIAL PRIMARY KEY,
    humidite1 DECIMAL(5, 2),
    humidite2 DECIMAL(5, 2),
    reduction DECIMAL(5, 2)
);

-- transformation
CREATE TABLE transformation (
    id SERIAL PRIMARY KEY,
    prix_unitaire NUMERIC(10, 2)
);

CREATE TABLE produit (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100),
    rendement NUMERIC(5, 2),
    prix_unitaire NUMERIC(10, 2)
);

CREATE SEQUENCE lot_paddy_reference_seq
START WITH 1
INCREMENT BY 1;

-- distribution
CREATE TABLE statut_distribution(
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255),
    sigle VARCHAR(255)
);

CREATE TABLE region(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255)
);

CREATE TABLE commune(
    id SERIAL PRIMARY KEY,
    id_region INT NOT NULL,
    nom VARCHAR(255),
    FOREIGN KEY (id_region) REFERENCES region(id)
);

CREATE TABLE lieu(
    id SERIAL PRIMARY KEY,
    id_commune INT NOT NULL,
    nom VARCHAR(255),
    FOREIGN KEY (id_commune) REFERENCES commune(id)
);

CREATE TABLE livreur(
    id SERIAL PRIMARY KEY,
    matricule_vehicule VARCHAR(255)
);

CREATE TABLE distribution(
    id SERIAL PRIMARY KEY,
    id_client INT NOT NULL,
    reference VARCHAR(255),
    id_livreur INT NOT NULL,
    id_lieu INT NOT NULL,
    date TIMESTAMP,
    FOREIGN KEY (id_client) REFERENCES client(id),
    FOREIGN KEY (id_livreur) REFERENCES livreur(id),
    FOREIGN KEY (id_lieu) REFERENCES lieu(id)
);

CREATE TABLE historique_distribution(
    id SERIAL PRIMARY KEY,
    id_distribution INT NOT NULL,
    id_statut_distribution INT NOT NULL,
    date TIMESTAMP,
    FOREIGN KEY (id_distribution) REFERENCES distribution(id),
    FOREIGN KEY (id_statut_distribution) REFERENCES statut_distribution(id)
);

CREATE TABLE detail_distribution(
    id SERIAL PRIMARY KEY,
    id_distribution INT NOT NULL,
    id_produit INT NOT NULL,
    quantite INT,
    date TIMESTAMP,
    FOREIGN KEY (id_distribution) REFERENCES distribution(id),
    FOREIGN KEY (id_produit) REFERENCES produit(id)
);

CREATE TABLE employee(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100),
    poste VARCHAR(100),
    telephone VARCHAR(100),
    email VARCHAR(100),
    date_embauche date,
    salaire NUMERIC(10, 2)
);

CREATE TABLE utilisateur(
    id SERIAL PRIMARY KEY,
    id_employee INT NOT NULL,
    nom VARCHAR(100),
    mdp VARCHAR(100),
    role VARCHAR(100),
    FOREIGN KEY (id_employee) REFERENCES employee(id)
);

-- A faire: dashboard tsy mandeh, les pages dans admin a trop de variables qui n;existe pas, ajouter un filtre dans sidebar selon le role