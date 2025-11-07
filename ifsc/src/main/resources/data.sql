/*
 * 1. INSERÇÃO DE PRODUTOS
 * Removendo a coluna "id". O banco de dados (H2) irá
 * atribuir automaticamente os IDs (1, 2, 3...).
 *

 */
/*
 * 1. INSERÇÃO DE PRODUTOS
 * CORREÇÃO: Coluna 'descricao' e seus valores removidos.
 */
INSERT INTO PRODUTO (nome, preco, ativo) VALUES
/* Produtos Antigos */
('Combo Califórnia', 49.90, true),
('Hot Philadelphia', 32.00, true),
('Temaki Salmão', 29.00, true),

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
('Hot Filadélfia (no data.sql)', 32.00, true), /* Nome ajustado para não duplicar 'Hot Philadelphia' */
('Hot Holl', 44.00, true),

/* TEMAKIS */
('Temaki Ebi', 42.00, true),
('Temaki Ebi Tempura', 40.00, true),
('Temaki Ebiten', 40.00, true),
('Temaki Filadélfia', 36.00, true),
('Temaki Hot', 40.00, true),
('Temaki Salmão (no data.sql)', 36.00, true), /* Nome ajustado para não duplicar 'Temaki Salmão' */
('Temaki Salmão Mix', 36.00, true),
('Temaki sem arroz', 49.00, true),
('Temaki Skin', 32.00, true),
('Temaki Sudarê', 54.00, true),
('Temaki Vegetariano ou Vegano', 32.00, true);

/*
 * 2. INSERÇÃO DE CLIENTES DE TESTE
 */
INSERT INTO CLIENTE (nome, email, telefone) VALUES
('Guilherme', 'gui@sudare.com', '(47) 99999-9999'),
('Patricia', 'patricia@sudare.com', '(47) 98888-8888');

/*
 * 3. INSERÇÃO DO CLIENTE "CONSUMIDOR FINAL"
 */
MERGE INTO CLIENTE (nome)
KEY(nome)
VALUES ('Consumidor Final');

/*
 * 2. INSERÇÃO DE CLIENTES DE TESTE
 * Mesmo princípio: removemos a coluna "id".
 * O H2 vai dar a eles os IDs 1 e 2.
 */
INSERT INTO CLIENTE (nome, email, telefone) VALUES
('Guilherme', 'gui@sudare.com', '(47) 99999-9999'),
('Patricia', 'patricia@sudare.com', '(47) 98888-8888');

/*
 * 3. INSERÇÃO DO CLIENTE "CONSUMIDOR FINAL" (A CORREÇÃO)
 * Usamos o comando MERGE (específico do H2) que é seguro:
 * - Ele usa a coluna 'nome' como chave.
 * - Se 'Consumidor Final' não existir, ele insere (e o H2 dará o ID 3).
 * - Se 'Consumidor Final' já existir, ele não faz nada.
 * Isso EVITA o erro de "Primary key violation" que estava quebrando sua aplicação.
 */
MERGE INTO CLIENTE (nome)
KEY(nome)
VALUES ('Consumidor Final');