INSERT INTO PRODUTO (nome, preco, ativo) VALUES
/* ENTRADAS */
('Tempura Camarão', 39.00, true),
('Bolinho de Peixe', 39.00, true),
('Guioza', 29.00, true),
('Sunomono', 9.00, true),
('Shimeji na manteiga', 40.00, true),

/* SASHIMIS */
('Carpaccio', 49.00, true),
('Ceviche', 49.00, true),
('Sashimi Salmão', 40.00, true),
('Sashimi Salmão Selado', 46.00, true),
('Tartar', 49.00, true),

/* HOSSOMAKIS */
('Hosso Filadélfia', 24.00, true),
('Hosso Kanimaki', 20.00, true),
('Hosso Kappamaki', 20.00, true),
('Hosso Sakemaki', 24.00, true),

/* URAMAKIS */
('Ura Califórnia', 28.00, true),
('Ura Ebiten', 34.00, true),
('Ura Salmão', 30.00, true),
('Ura Skin', 28.00, true),
('Ura Sudarê', 44.00, true),
('Ura Vegetariano ou Vegano', 28.00, true),
('Ura Filadélfia', 30.00, true),

/* FUTOMAKIS */
('Futomaki Especial', 44.00, true),
('Futomaki Salmão, Skin, Cream Cheese, Rúcula e Cebolinha', 38.00, true),

/* NIGUIRIS */
('Niguiri Camarão', 34.00, true),
('Niguiri Kani', 20.00, true),
('Niguiri Salmão', 28.00, true),
('Niguiri Salmão Selado', 32.00, true),
('Niguiri Skin', 24.00, true),

/* GUNKANS */
('Gunkan Camarão', 38.00, true),
('Gunkan Gorgonzola', 38.00, true),
('Gunkan Shake Negui', 32.00, true),
('Robata de Camarão', 38.00, true),

/* COMBINADOS */
('Combinado 24 peças', 99.00, true),
('Combinado 50 peças + Hot', 189.00, true),
('Combinado 80 peças', 259.00, true),
('Combinado Vegano ou Vegetariano 24 peças', 79.00, true),

/* HOTS */
('Hot Camarão', 36.00, true),
('Hot Filadélfia', 32.00, true), 
('Hot Holl', 44.00, true),

/* TEMAKIS */
('Temaki Ebi', 42.00, true),
('Temaki Ebi Tempura', 40.00, true),
('Temaki Ebiten', 40.00, true),
('Temaki Filadélfia', 36.00, true),
('Temaki Hot', 40.00, true),
('Temaki Salmão', 36.00, true), 
('Temaki Salmão Mix', 36.00, true),
('Temaki sem arroz', 49.00, true),
('Temaki Skin', 32.00, true),
('Temaki Sudarê', 54.00, true),
('Temaki Vegetariano ou Vegano', 32.00, true);

/* INSERÇÃO DE CLIENTES DE TESTE*/
INSERT INTO CLIENTE (nome, email, telefone) VALUES
('Guilherme', 'gui@sudare.com', '(47) 99999-9999'),
('Patricia', 'patricia@sudare.com', '(47) 98888-8888');

/*3. INSERÇÃO DO CLIENTE "CONSUMIDOR FINAL"*/
MERGE INTO CLIENTE (nome)
KEY(nome)
VALUES ('Consumidor Final');