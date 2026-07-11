-- Supprime uniquement les lignes identifiées par le préfixe DEMO-STAT-.
-- Les types de transaction et produits peuvent être partagés avec les autres modules et sont conservés.

DELETE FROM detail_lot_transforme
WHERE id_lot_transforme IN (
    SELECT id FROM lot_paddy_transforme WHERE reference LIKE 'DEMO-STAT-%'
);

DELETE FROM lot_paddy_transforme WHERE reference LIKE 'DEMO-STAT-%';
DELETE FROM lot_paddy WHERE reference LIKE 'DEMO-STAT-%';

DELETE FROM detail_transaction
WHERE id_transaction IN (
    SELECT id FROM "transaction" WHERE reference LIKE 'DEMO-STAT-%'
);

DELETE FROM "transaction" WHERE reference LIKE 'DEMO-STAT-%';
DELETE FROM fourniture WHERE nom LIKE 'DEMO-STAT-%';
