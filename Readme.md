sgb_biblioteca/
├── build.gradle              # Configuração do projeto e dependências (JavaFX, JDBC, Flyway)
├── README.md                 # Este arquivo
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java      # Configuração do módulo Java (JPMS)
        │   └── com/sgbu/
        │       ├── app/              # Classes de inicialização e configuração (e.g., MainApplication)
        │       ├── model/            # Classes de modelo de dados (POJOs/Entidades)
        │       │   └── Usuario.java
        │       ├── dao/              # Data Access Objects (DAO) e ConnectionFactory
        │       │   ├── ConnectionFactory.java
        │       │   └── UsuarioDAO.java
        │       ├── service/          # Camada de Serviço/Regras de Negócio (e.g., AuthService)
        │       │   └── AuthService.java
        │       └── ui/               # Controladores da Interface Gráfica (JavaFX Controllers)
        │           └── LoginController.java
        └── resources/
            ├── config.properties     # Configurações de conexão com o BD
            ├── db/
            │   └── migration/        # Scripts de migração do Flyway
            │       └── V1__Initial_Schema.sql
            └── fxml/                 # Arquivos FXML para a interface gráfica
                └── LoginView.fxml

                | Módulo | Nome da Branch | Objetivo |
| :--- | :--- | :--- |
| **Base** | `main` ou `master` | *Branch* principal, estável e de produção. |
| **Autenticação** | `feat/auth-login` | Implementação do ecrã de Login e lógica de autenticação. |
| **Utilizadores** | `feat/user-management` | CRUD de Administradores, Bibliotecários e Leitores. |
| **Catálogo/Acervo** | `feat/catalog-management` | CRUD de Obras e Exemplares. |
| **Circulação** | `feat/circulation` | Lógica de Empréstimos, Devoluções e Renovações. |
| **Reservas** | `feat/reservations` | Gestão da fila de espera e notificação de reservas. |
| **Multas** | `feat/fines-management` | Cálculo e gestão de multas por atraso/dano. |
| **Relatórios** | `feat/reports` | Implementação dos ecrãs e lógica de geração de relatórios. |
