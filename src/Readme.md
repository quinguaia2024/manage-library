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