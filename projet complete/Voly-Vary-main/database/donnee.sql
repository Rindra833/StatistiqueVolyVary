INSERT INTO client (reference, nom, prenom, telephone, date) VALUES
('CL001', 'Rakoto', 'Jean', '0341234567', '2025-07-01'),
('CL002', 'Rabe', 'Marie', '0349876543', '2025-07-01'),
('CL003', 'Andrianarisoa', 'Hery', '0345678901', '2025-07-01');

INSERT INTO type_transaction (libelle) VALUES 
('Vente'),
('Vente à crédit'),
('Location');

INSERT INTO statut_transaction (libelle, sigle) VALUES 
('En attente', 'ATT'),
('Validée', 'VAL');

INSERT INTO categorie_fourniture (libelle) VALUES 
('Semences'),
('Engrais'),
('Matériel');


INSERT INTO fourniture (id_categorie, reference, prix_unitaire) VALUES 
(1, 'SEM-001', 1500.00),
(1, 'SEM-002', 1800.00),
(2, 'ENG-001', 2500.00),
(3, 'MAT-001', 5000.00);


INSERT INTO statut_lot_paddy (libelle, sigle) VALUES
('En attente de validation', 'EN_ATTENTE'),
('Validé et payé', 'VALIDE'),
('Annulé', 'ANNULE');


INSERT INTO reduction (humidite1, humidite2, reduction) VALUES
(0, 15, 0),
(15, 20, 20),
(20, 100, 50);

INSERT INTO lot_paddy (reference , quantite) VALUES
('LP001' , 50),
('LP002' , 19),
('LP003' , 60),
('LP004' , 50),
('LP005' , 50),
('LP006' , 19),
('LP007' , 60),
('LP008' , 50),
('LP009' , 50),
('LP010' , 19),
('LP011' , 60),
('LP012' , 50);

INSERT INTO produit(nom , prix_unitaire , rendement) VALUES
('Vary' , 100 , 65),
('Akofom-bary', 50 , 20),
('Tofom-bary' , 25 , 10),
('vary mandinika' , 10 , 5);

INSERT INTO transformation(prix_unitaire) VALUES (100);

INSERT INTO region (nom) VALUES ('Analamanga');
INSERT INTO commune (id_region, nom) VALUES (1, 'Antananarivo');
INSERT INTO lieu (id_commune, nom) VALUES (1, 'Andoharanofotsy');
INSERT INTO livreur (matricule_vehicule) VALUES ('7714 TAV');
INSERT INTO statut_distribution (libelle, sigle) VALUES ('En cours', 'EN_COURS'), ('Terminé', 'TERMINE'), ('Annulé', 'ANNULE');

INSERT INTO employee(nom,poste,telephone,email,date_embauche,salaire) VALUES
('Jean Paul','poste 1','033 00 000 00','jean@gmail.com','2025-01-01',10000);

CREATE TABLE utilisateur(
    id SERIAL PRIMARY KEY,
    id_employee INT NOT NULL,
    nom VARCHAR(100),
    mdp VARCHAR(100),
    role VARCHAR(100),
    FOREIGN KEY (id_employee) REFERENCES employee(id)
);