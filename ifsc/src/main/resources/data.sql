/* ========================================================== */
/* === PRODUTOS === */
/* ========================================================== */
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

/* ========================================================== */
/* === CLIENTES === */
/* ========================================================== */
INSERT INTO CLIENTE (nome, email, telefone) VALUES
('Guilherme', 'gui@sudare.com', '(47) 99999-9999'),
('Patricia', 'patricia@sudare.com', '(47) 98888-8888');

MERGE INTO CLIENTE (nome) KEY(nome) VALUES ('Consumidor Final');

/* ========================================================== */
/* === PEDIDOS DE TESTE === */
/* CORREÇÃO: Adicionado 'taxa_servico' (false) em todos os inserts */
/* ========================================================== */

/* --- PEDIDO 1: FINALIZADO (Hoje) --- */
INSERT INTO PEDIDO (cliente_id, nome_cliente_observacao, status, total, taxa_servico, criado_em, atualizado_em) VALUES 
((SELECT id FROM CLIENTE WHERE nome = 'Consumidor Final'), 'Pedido de Hoje', 'FINALIZADO', 87.00, false,
/* --- PEDIDO 1: FINALIZADO (Hoje) --- */
INSERT INTO PEDIDO (nome_cliente_observacao, status, total, criado_em, atualizado_em) VALUES 
('Pedido de Hoje', 'FINALIZADO', 87.00, 
CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO ITEM_PEDIDO (pedido_id, produto_id, quantidade, preco_unitario) VALUES 
(1, (SELECT id FROM PRODUTO WHERE nome = 'Tempura Camarão'), 1, 39.00),
(1, (SELECT id FROM PRODUTO WHERE nome = 'Hosso Sakemaki'), 2, 24.00);

/* --- PEDIDO 2: FINALIZADO (Semana Passada - 3 dias atrás) --- */
INSERT INTO PEDIDO (cliente_id, nome_cliente_observacao, status, total, taxa_servico, criado_em, atualizado_em) VALUES 
((SELECT id FROM CLIENTE WHERE nome = 'Guilherme'), null, 'FINALIZADO', 189.00, false,
INSERT INTO PEDIDO (nome_cliente_observacao, status, total, criado_em, atualizado_em) VALUES 
('Guilherme', 'FINALIZADO', 189.00, 
DATEADD('DAY', -3, CURRENT_TIMESTAMP()), DATEADD('DAY', -3, CURRENT_TIMESTAMP()));

INSERT INTO ITEM_PEDIDO (pedido_id, produto_id, quantidade, preco_unitario) VALUES 
(2, (SELECT id FROM PRODUTO WHERE nome = 'Combinado 50 peças + Hot'), 1, 189.00);

/* --- PEDIDO 3: FINALIZADO (Mês Atual - 10 dias atrás) --- */
INSERT INTO PEDIDO (cliente_id, nome_cliente_observacao, status, total, taxa_servico, criado_em, atualizado_em) VALUES 
((SELECT id FROM CLIENTE WHERE nome = 'Patricia'), 'Aniversário', 'FINALIZADO', 100.00, true, -- Exemplo com taxa TRUE
INSERT INTO PEDIDO (nome_cliente_observacao, status, total, criado_em, atualizado_em) VALUES 
('Patricia (Aniversário)', 'FINALIZADO', 100.00, 
DATEADD('DAY', -10, CURRENT_TIMESTAMP()), DATEADD('DAY', -10, CURRENT_TIMESTAMP()));

INSERT INTO ITEM_PEDIDO (pedido_id, produto_id, quantidade, preco_unitario) VALUES 
(3, (SELECT id FROM PRODUTO WHERE nome = 'Hot Filadélfia'), 2, 32.00),
(3, (SELECT id FROM PRODUTO WHERE nome = 'Temaki Salmão'), 1, 36.00);

/* --- PEDIDO 4: FINALIZADO (Antigo - 40 dias atrás) --- */
INSERT INTO PEDIDO (cliente_id, nome_cliente_observacao, status, total, taxa_servico, criado_em, atualizado_em) VALUES 
((SELECT id FROM CLIENTE WHERE nome = 'Consumidor Final'), 'Pedido Antigo', 'FINALIZADO', 40.00, false,
INSERT INTO PEDIDO (nome_cliente_observacao, status, total, criado_em, atualizado_em) VALUES 
('Pedido Antigo', 'FINALIZADO', 40.00, 
DATEADD('DAY', -40, CURRENT_TIMESTAMP()), DATEADD('DAY', -40, CURRENT_TIMESTAMP()));

INSERT INTO ITEM_PEDIDO (pedido_id, produto_id, quantidade, preco_unitario) VALUES 
(4, (SELECT id FROM PRODUTO WHERE nome = 'Sashimi Salmão'), 1, 40.00);

/* --- PEDIDO 5: EM_PREPARO (Hoje - Para a Home) --- */
INSERT INTO PEDIDO (nome_cliente_observacao, status, total, criado_em, atualizado_em) VALUES 
('Pedido para a fila', 'EM_PREPARO', 29.00, 
DATEADD('MINUTE', -10, CURRENT_TIMESTAMP()), DATEADD('MINUTE', -10, CURRENT_TIMESTAMP()));

INSERT INTO ITEM_PEDIDO (pedido_id, produto_id, quantidade, preco_unitario) VALUES 
(5, (SELECT id FROM PRODUTO WHERE nome = 'Guioza'), 1, 29.00);

/* --- PEDIDO 6: ABERTO (Hoje - Para a Home) --- */
INSERT INTO PEDIDO (nome_cliente_observacao, status, total, criado_em, atualizado_em) VALUES 
('Acabou de abrir', 'ABERTO', 9.00, 
DATEADD('MINUTE', -5, CURRENT_TIMESTAMP()), DATEADD('MINUTE', -5, CURRENT_TIMESTAMP()));

INSERT INTO ITEM_PEDIDO (pedido_id, produto_id, quantidade, preco_unitario) VALUES 
(6, (SELECT id FROM PRODUTO WHERE nome = 'Sunomono'), 1, 9.00);

/* --- PEDIDO 7: PRONTO (Hoje - Para a Home/Fila) --- */
INSERT INTO PEDIDO (nome_cliente_observacao, status, total, criado_em, atualizado_em) VALUES 
('Patricia (Pedido PRONTO)', 'PRONTO', 30.00, 
DATEADD('MINUTE', -20, CURRENT_TIMESTAMP()), DATEADD('MINUTE', -5, CURRENT_TIMESTAMP()));

INSERT INTO ITEM_PEDIDO (pedido_id, produto_id, quantidade, preco_unitario) VALUES 
(7, (SELECT id FROM PRODUTO WHERE nome = 'Ura Salmão'), 1, 30.00);