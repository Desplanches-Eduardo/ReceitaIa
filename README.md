# ReceitaIa

API REST para guardar o que tem na despensa (nome, categoria, quantidade e validade do item). A ideia
do projeto é, mais para frente, sugerir receitas a partir do que já existe em casa.

## Stack

- Java 21 e Spring Boot 4.1.1 (Maven, wrapper incluído)
- Spring Data JPA com Hibernate
- Flyway para as migrations do banco
- H2 em memória no ambiente de desenvolvimento
- Lombok

## Como rodar

No terminal, dentro da pasta do projeto:

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

Para rodar os testes:

```bash
./mvnw test
```

Console do H2, útil para olhar as tabelas enquanto a aplicação está de pé: `http://localhost:8080/h2-console`,
com JDBC URL `jdbc:h2:mem:testdb`, usuário `sa` e senha em branco.

## Endpoints

| Método | Rota | O que faz | Resposta |
| --- | --- | --- | --- |
| `POST` | `/food` | cadastra um item | `201` com mensagem de confirmação |
| `GET` | `/food` | lista todos os itens | `200` com a lista |
| `GET` | `/food/{id}` | busca um item pelo id | `200` ou `404` |
| `PUT` | `/food/{id}` | atualiza um item | `200` ou `404` |
| `DELETE` | `/food/{id}` | remove um item | `204` |

Exemplo de cadastro:

```bash
curl -X POST http://localhost:8080/food \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Arroz","categoria":"CEREAIS","quantidade":3,"validade":"2027-04-27"}'
```

A categoria vem do enum `Categoria` (cereais, tubérculos, leguminosas, verduras, legumes, frutas,
proteínas, gorduras, doces, bebidas e temperos) e é gravada no banco como texto.

## Banco de dados

O schema é criado pelas migrations do Flyway, em `src/main/resources/db/migration`. O Hibernate roda com
`spring.jpa.hibernate.ddl-auto=validate`: ele apenas confere se as entidades batem com as tabelas e falha
na inicialização quando algo diverge, em vez de alterar a estrutura por conta própria. Migração nova
entra como arquivo novo (`V2__descricao.sql`) e uma migration que já rodou não deve ser editada, porque o
Flyway guarda o checksum dela.

## Estrutura

```
src/main/java/Desplanchescom/example/ReceitaIa/
├── controller/   FoodItemController   rotas HTTP
├── service/      FoodItemService      regras de negócio
├── repository/   FoodItemRepository   acesso ao banco
├── Mapper/       FoodItemMapper       conversão entidade <-> DTO
├── DTO/          FoodItemDTO          dados que entram e saem da API
├── model/        FoodItemModel        entidade JPA
└── enums/        Categoria            categoria de alimento
src/main/resources/
├── application.properties
└── db/migration/  migrations do Flyway
```

## Dados sensíveis

O `.gitignore` cobre `.env`, arquivos de chave (`*.pem`, `*.key`, `*.p12`, `*.jks`, `*.keystore`),
configuração local (`application-local.properties`) e de segredo (`application-secret*.properties`),
arquivos do banco H2 local e logs. Credencial de produção deve chegar por variável de ambiente, não por
arquivo versionado.
