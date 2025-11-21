import pytest
import requests

# fixtures = aumenta a cobertura de codigo/unidade reutilizavel
@pytest.fixture
def base_url():
    return "http://localhost:8080"

# TESTES DE GET

# TESTE 1
@pytest.mark.parametrize("endpoint", ["/api/produtos", "/api/pedidos"])
def test_leitura_status_200(base_url, endpoint):
    "verifica se as listagens principais carregam(get)"
    url = f"{base_url}{endpoint}"
    response = requests.get(url)
    assert response.status_code == 200
    #metodo de ensaio que valida informacoes

# TESTE 2
@pytest.mark.parametrize("id_invalido", [99999, -1])
def test_erro_busca_inexistente(base_url, id_invalido):
    "verifica se o sistema trata erros de forma certa (404/400)"
    url = f"{base_url}/api/pedidos/{id_invalido}"
    response = requests.get(url)
    assert response.status_code in [404, 400]
    #metodo de ensaio que valida informacoes 
    
# TESTES DE POST

# TESTE 3
def test_criar_novo_produto(base_url):
    "teste de integração: envia um JSON para criar um produto e valida se ele foi salvo."
    url = f"{base_url}/api/produtos"
    
    # JSON do novo produto
    novo_produto = {
        "nome": "Sushi Teste Python",
        "categoria": "ENTRADAS",
        "preco": 50.00,
        "ativo": True
    }
    
    # envia o POST
    response = requests.post(url, json=novo_produto)
    
    # se retornar 201 e porque criou o produto
    assert response.status_code == 201
    
    # converte a resposta em JSON
    produto_criado = response.json()
    #verifica com o assert se o produto foi criado e retorna o id
    assert "id" in produto_criado
    assert produto_criado["nome"] == "Sushi Teste Python"

# TESTE 4
def test_atualizar_status_pedido(base_url):
    "teste de fluxo: pega o primeiro pedido da lista e tenta mudar o status dele."
    # busca pedido existente para usar no teste
    lista_response = requests.get(f"{base_url}/api/pedidos")
    pedidos = lista_response.json()
    
    # se nao tiver nenhum pedido, o teste nao continua
    if len(pedidos) > 0:
        id_pedido = pedidos[0]["id"]
        
        # atualiza o status para entregue
        url_update = f"{base_url}/api/pedidos/{id_pedido}/status"
        response = requests.patch(url_update, params={"status": "ENTREGUE"})
        
        # verifica se deu certo com o assert
        assert response.status_code == 200
        assert response.json()["status"] == "ENTREGUE"
    else:
        pytest.skip("Não há pedidos cadastrados para testar a atualização")