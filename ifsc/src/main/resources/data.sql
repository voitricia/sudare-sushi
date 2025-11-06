/*
 * 1. INSERÇÃO DE PRODUTOS
 * Removendo a coluna "id". O banco de dados (H2) irá
 * atribuir automaticamente os IDs (1, 2, 3...).
 */
INSERT INTO PRODUTO (nome, descricao, preco, estoque, ativo) VALUES
('Combo Califórnia', '8 uramakis + 4 niguiris', 49.90, 50, true),
('Hot Philadelphia', '10 unidades', 32.00, 40, true),
('Temaki Salmão', 'Temaki de salmão com cebolinha', 29.00, 30, true);

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