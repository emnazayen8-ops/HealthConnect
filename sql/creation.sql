CREATE DATABASE IF NOT EXISTS healthconnect
    CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE healthconnect;

CREATE TABLE IF NOT EXISTS clinique (
                                        id VARCHAR(10) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse VARCHAR(150),
    telephone VARCHAR(15)
    );

CREATE TABLE IF NOT EXISTS laboratoire (
                                           id VARCHAR(10) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse VARCHAR(150),
    telephone VARCHAR(15)
    );

CREATE TABLE IF NOT EXISTS centre_radiologie (
                                                 id VARCHAR(10) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse VARCHAR(150),
    telephone VARCHAR(15)
    );

CREATE TABLE IF NOT EXISTS pharmacie (
                                         id VARCHAR(10) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse VARCHAR(150),
    telephone VARCHAR(15)
    );

CREATE TABLE IF NOT EXISTS medecin (
                                       id VARCHAR(10) PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    specialite VARCHAR(80),
    clinique_id VARCHAR(10) NOT NULL,
    FOREIGN KEY (clinique_id) REFERENCES clinique(id)
    );

CREATE TABLE IF NOT EXISTS patient (
                                       id VARCHAR(10) PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    date_naissance DATE NOT NULL,
    sexe CHAR(1) NOT NULL,
    telephone VARCHAR(15),
    antecedents VARCHAR(200)
    );

CREATE TABLE IF NOT EXISTS utilisateur (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           login VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    patient_id VARCHAR(10),
    medecin_id VARCHAR(10),
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (medecin_id) REFERENCES medecin(id)
    );

CREATE TABLE IF NOT EXISTS consultation (
                                            id VARCHAR(10) PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    medecin_id VARCHAR(10) NOT NULL,
    date_consultation DATE NOT NULL,
    motif VARCHAR(200) NOT NULL,
    diagnostic VARCHAR(200),
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (medecin_id) REFERENCES medecin(id)
    );

CREATE TABLE IF NOT EXISTS prescription (
                                            id VARCHAR(10) PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    medecin_id VARCHAR(10) NOT NULL,
    consultation_id VARCHAR(10) NOT NULL,
    date_prescription DATE NOT NULL,
    analyses_demandees VARCHAR(200),
    examens_demandes VARCHAR(200),
    medicaments VARCHAR(200),
    pharmacie_id VARCHAR(10),
    statut VARCHAR(20) NOT NULL DEFAULT 'creee',
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (medecin_id) REFERENCES medecin(id),
    FOREIGN KEY (consultation_id) REFERENCES consultation(id),
    FOREIGN KEY (pharmacie_id) REFERENCES pharmacie(id)
    );

CREATE TABLE IF NOT EXISTS analyse (
                                       id VARCHAR(10) PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    prescription_id VARCHAR(10) NOT NULL,
    laboratoire_id VARCHAR(10) NOT NULL,
    nom_test VARCHAR(100) NOT NULL,
    date_analyse DATE,
    valeur DECIMAL(8,2),
    unite VARCHAR(20),
    valeur_normale VARCHAR(30),
    critique BOOLEAN NOT NULL DEFAULT FALSE,
    statut VARCHAR(20) NOT NULL DEFAULT 'demandee',
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (prescription_id) REFERENCES prescription(id),
    FOREIGN KEY (laboratoire_id) REFERENCES laboratoire(id)
    );

CREATE TABLE IF NOT EXISTS radiologie (
                                          id VARCHAR(10) PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    prescription_id VARCHAR(10) NOT NULL,
    centre_id VARCHAR(10) NOT NULL,
    type_examen VARCHAR(30) NOT NULL,
    region_examinee VARCHAR(50),
    date_examen DATE,
    description TEXT,
    conclusion TEXT,
    radiologue VARCHAR(100),
    statut VARCHAR(20) NOT NULL DEFAULT 'demandee',
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (prescription_id) REFERENCES prescription(id),
    FOREIGN KEY (centre_id) REFERENCES centre_radiologie(id)
    );

CREATE TABLE IF NOT EXISTS suivi (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     patient_id VARCHAR(10) NOT NULL,
    consultation_id VARCHAR(10) NOT NULL,
    date_suivi DATE NOT NULL,
    observation VARCHAR(300),
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (consultation_id) REFERENCES consultation(id)
    );

CREATE TABLE IF NOT EXISTS notification (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            destinataire_type VARCHAR(20) NOT NULL,
    destinataire_id VARCHAR(10) NOT NULL,
    type VARCHAR(30) NOT NULL,
    message VARCHAR(300) NOT NULL,
    date_envoi DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lue BOOLEAN NOT NULL DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS journal_acces (
                                             id INT AUTO_INCREMENT PRIMARY KEY,
                                             utilisateur_id INT NOT NULL,
                                             patient_id VARCHAR(10) NOT NULL,
    action VARCHAR(50) NOT NULL,
    date_action DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id),
    FOREIGN KEY (patient_id) REFERENCES patient(id)
    );