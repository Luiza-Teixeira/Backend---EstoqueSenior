# Backend---EstoqueSenior
Aplicação web para gestão de estoque e vendas de pequenos comércios, com modos Padrão e Simplificado e foco em acessibilidade para pessoas idosas.

## Requisitos do Sistema

### Requisitos Funcionais

- **RF01 — Cadastrar produtos:** O sistema deve permitir o cadastro de produtos com as informações necessárias para o controle de estoque.

- **RF02 — Consultar produtos:** O sistema deve permitir a consulta dos produtos cadastrados e de suas respectivas quantidades em estoque.

- **RF03 — Registrar entrada de mercadorias:** O sistema deve permitir registrar a entrada de novas unidades de um produto, atualizando automaticamente seu estoque.

- **RF04 — Registrar vendas:** O sistema deve permitir registrar a venda de produtos e atualizar automaticamente a quantidade disponível em estoque.

- **RF05 — Validar estoque:** O sistema não deve permitir uma venda quando a quantidade solicitada for superior à quantidade disponível em estoque.

- **RF06 — Consultar movimentações:** O sistema deve permitir consultar o histórico de entradas e saídas de produtos.

- **RF07 — Identificar estoque baixo:** O sistema deve permitir identificar produtos com baixo nível de estoque.

- **RF08 — Disponibilizar Modo Padrão:** O sistema deve oferecer um modo com acesso às funcionalidades e informações completas de gerenciamento, destinado a usuários com maior familiaridade com tecnologia.

- **RF09 — Disponibilizar Modo Simplificado:** O sistema deve oferecer um modo simplificado, direcionado principalmente a pessoas idosas e usuários com menor familiaridade com tecnologia, apresentando as operações essenciais de maneira mais simples e direta.

- **RF10 — Compartilhar dados entre os modos:** Os modos Padrão e Simplificado devem utilizar os mesmos dados de produtos, vendas, movimentações e estoque.


### Requisitos Não Funcionais

- **RNF01 — Responsividade:** A aplicação deve possuir interface web responsiva, permitindo sua utilização em computadores e dispositivos móveis.

- **RNF02 — Acessibilidade:** O Modo Simplificado deve considerar critérios de acessibilidade adequados ao público idoso, incluindo legibilidade, contraste, tamanho dos elementos de interação e compreensão da linguagem.

- **RNF03 — Usabilidade:** O Modo Simplificado deve priorizar fluxos curtos, linguagem direta e redução de informações e opções desnecessárias para a realização das tarefas essenciais.

- **RNF04 — Consistência dos dados:** As alterações de estoque realizadas por entradas e vendas devem permanecer consistentes independentemente do modo utilizado.

- **RNF05 — Persistência:** Os dados necessários ao funcionamento do sistema devem ser armazenados de forma persistente.


## Arquitetura

O sistema será desenvolvido utilizando uma arquitetura em camadas baseada no padrão MVC, buscando separar as responsabilidades da aplicação e facilitar sua organização, manutenção e testes.

No backend, a aplicação será organizada principalmente nas seguintes camadas:

- **Modelo (Model):** representa as entidades e os dados do sistema, como Produto e Venda.
- **Controlador (Controller):** recebe as requisições da aplicação e encaminha as operações para a camada de serviço.
- **Serviço (Service):** concentra as regras de negócio do sistema.
- **Repositório (Repository):** realiza o acesso e a persistência dos dados no banco de dados.
