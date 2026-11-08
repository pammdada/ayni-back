-- El frontend ya captura y muestra el sector/barrio del perfil.
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS sector VARCHAR(120);
