USE healthconnect;

INSERT INTO clinique (id, nom, adresse, telephone) VALUES
    ('CL001', 'Clinique Les Oliviers', 'Route de Tunis, Sfax', '74111222');

INSERT INTO laboratoire (id, nom, adresse, telephone) VALUES
    ('LAB001', 'Laboratoire Al Shifa', 'Avenue Hedi Chaker, Sfax', '74222333');

INSERT INTO centre_radiologie (id, nom, adresse, telephone) VALUES
    ('RAD001', 'Centre Imagerie Sfax', 'Rue Mahdia, Sfax', '74333444');

INSERT INTO pharmacie (id, nom, adresse, telephone) VALUES
                                                        ('PH001', 'Pharmacie Centrale', 'Avenue Habib Bourguiba, Sfax', '74123456'),
                                                        ('PH002', 'Pharmacie El Amel', 'Route de Gabes, Sfax', '74654321');

INSERT INTO medecin (id, nom, prenom, specialite, clinique_id) VALUES
                                                                   ('MED001', 'Trabelsi', 'Sami', 'Médecine générale', 'CL001'),
                                                                   ('MED002', 'Karray', 'Nadia', 'Endocrinologie', 'CL001');

INSERT INTO patient (id, nom, prenom, date_naissance, sexe, telephone, antecedents) VALUES
                                                                                        ('P001', 'Ben Ali', 'Ahmed', '1985-05-12', 'M', '20123456', 'Diabète'),
                                                                                        ('P002', 'Gharbi', 'Salma', '1992-03-08', 'F', '22987654', NULL),
                                                                                        ('P003', 'Jlassi', 'Karim', '1970-09-25', 'M', NULL, 'Diabète, Hypertension');

INSERT INTO consultation (id, patient_id, medecin_id, date_consultation, motif, diagnostic) VALUES
                                                                                                ('C001', 'P001', 'MED001', '2026-11-20', 'Fièvre et fatigue', 'Infection virale'),
                                                                                                ('C002', 'P002', 'MED001', '2026-11-21', 'Toux persistante', NULL),
                                                                                                ('C003', 'P003', 'MED001', '2026-11-22', 'Soif intense et vision floue', NULL);

INSERT INTO prescription (id, patient_id, medecin_id, consultation_id, date_prescription,
                          analyses_demandees, examens_demandes, medicaments, pharmacie_id, statut) VALUES
                                                                                                       ('PR001', 'P001', 'MED001', 'C001', '2026-11-20',
                                                                                                        'Glycémie à jeun', 'Radio du thorax', 'Paracétamol 1 g', 'PH001', 'envoyee'),
                                                                                                       ('PR002', 'P003', 'MED001', 'C003', '2026-11-22',
                                                                                                        'Glycémie à jeun, HbA1c', NULL, NULL, NULL, 'creee'),
                                                                                                       ('PR003', 'P002', 'MED001', 'C002', '2026-11-27',
                                                                                                        'NFS, CRP', 'Radio du thorax, Echographie abdominale', NULL, NULL, 'creee');

INSERT INTO analyse (id, patient_id, prescription_id, laboratoire_id, nom_test,
                     date_analyse, valeur, unite, valeur_normale, critique, statut) VALUES
                                                                                        ('A001', 'P001', 'PR001', 'LAB001', 'Glycémie', '2026-11-21', 1.40, 'g/L', '0.7 - 1.1', FALSE, 'validee'),
                                                                                        ('A002', 'P003', 'PR002', 'LAB001', 'Glycémie', '2026-11-23', 3.20, 'g/L', '0.7 - 1.1', TRUE, 'validee'),
                                                                                        ('A003', 'P003', 'PR002', 'LAB001', 'HbA1c', '2026-11-23', 9.80, '%', '4 - 6', TRUE, 'validee');

INSERT INTO radiologie (id, patient_id, prescription_id, centre_id, type_examen, region_examinee,
                        date_examen, description, conclusion, radiologue, statut) VALUES
    ('R001', 'P001', 'PR001', 'RAD001', 'Radio', 'Thorax', '2026-11-21',
     'Pas d''anomalie visible', 'Examen normal', 'Dr Leila Mansour', 'validee');

INSERT INTO suivi (patient_id, consultation_id, date_suivi, observation) VALUES
    ('P003', 'C003', '2026-12-05', 'Contrôle de la glycémie après traitement');

INSERT INTO notification (destinataire_type, destinataire_id, type, message) VALUES
                                                                                 ('MEDECIN', 'MED001', 'resultat_critique', 'Glycémie critique pour le patient P003'),
                                                                                 ('PATIENT', 'P003', 'resultat_critique', 'Un de vos résultats nécessite une consultation rapide');