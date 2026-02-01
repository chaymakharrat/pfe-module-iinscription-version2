# Camunda Microservice

Microservice standalone Camunda BPM pour la gestion des processus métier dans une architecture microservices.

## 📋 Table des matières

- [Technologies](#technologies)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Démarrage](#démarrage)
- [Endpoints REST](#endpoints-rest)
- [Intégration avec Feign](#intégration-avec-feign)
- [Camunda Webapp](#camunda-webapp)
- [Tests](#tests)

## 🛠 Technologies

- **Spring Boot**: 2.7.18
- **Camunda BPM**: 7.18.0 (compatible avec javax.* packages)
- **Java**: 11+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Spring Cloud OpenFeign**: 2021.0.8

## 📦 Prérequis

1. **Java 11** ou supérieur
2. **Maven 3.6+**
3. **MySQL 8.0+**
4. **Git** (optionnel)

## 🚀 Installation

### 1. Créer la base de données MySQL

```sql
CREATE DATABASE camunda CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configurer les credentials

Modifier le fichier `src/main/resources/application.yml` :

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/camunda?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root  # Modifier selon votre configuration
    password: root  # Modifier selon votre configuration
```

### 3. Compiler le projet

```bash
cd camunda-service
mvn clean install
```

## ⚙️ Configuration

### Application.yml

Le fichier `application.yml` contient toutes les configurations nécessaires :

- **Port**: 8085
- **Base de données**: MySQL
- **Utilisateur admin Camunda**: admin/admin
- **Auto-déploiement des processus BPMN**: activé
- **Niveau d'historique**: full

### Utilisateur Admin par défaut

- **Username**: admin
- **Password**: admin

> ⚠️ **Important**: Changez ces credentials en production !

## 🏃 Démarrage

### Méthode 1: Avec Maven

```bash
mvn spring-boot:run
```

### Méthode 2: Avec JAR

```bash
mvn clean package
java -jar target/camunda-service-1.0.0.jar
```

### Vérification du démarrage

Le service démarre sur le port **8085**. Vérifiez les logs :

```
Camunda BPM Platform initialized successfully
Started CamundaServiceApplication in X.XXX seconds
```

## 🌐 Endpoints REST

### 1. Démarrer un processus

**POST** `/api/process/start`

**Body**:
```json
{
  "processDefinitionKey": "inscription-process",
  "businessKey": "INS-2026-001",
  "variables": {
    "studentId": "12345",
    "studentName": "Jean Dupont",
    "assignee": "admin"
  }
}
```

**Response**:
```json
{
  "processInstanceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "processDefinitionId": "inscription-process:1:xyz",
  "businessKey": "INS-2026-001",
  "status": "STARTED"
}
```

### 2. Récupérer les tâches d'un utilisateur

**GET** `/api/tasks/user/{userId}`

**Exemple**: `/api/tasks/user/admin`

**Response**:
```json
[
  {
    "id": "task-123",
    "name": "Vérifier les données",
    "assignee": "admin",
    "created": "2026-01-30T12:00:00.000+00:00",
    "processInstanceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "processDefinitionKey": "inscription-process:1:xyz",
    "variables": {
      "studentId": "12345",
      "studentName": "Jean Dupont"
    }
  }
]
```

### 3. Compléter une tâche

**POST** `/api/tasks/{taskId}/complete`

**Body**:
```json
{
  "variables": {
    "approved": true,
    "comment": "Données validées"
  }
}
```

**Response**:
```json
{
  "taskId": "task-123",
  "status": "COMPLETED"
}
```

### 4. Récupérer les tâches d'un processus

**GET** `/api/tasks/process/{processInstanceId}`

### 5. Récupérer les tâches non assignées

**GET** `/api/tasks/unassigned`

### 6. Assigner une tâche à un utilisateur

**POST** `/api/tasks/{taskId}/claim/{userId}`

## 🔗 Intégration avec Feign

### Dans vos autres microservices

#### 1. Ajouter la dépendance Feign

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### 2. Copier l'interface Feign Client

Copiez les fichiers suivants dans votre microservice :
- `CamundaFeignClient.java`
- `StartProcessRequest.java`
- `TaskDto.java`
- `CompleteTaskRequest.java`

#### 3. Activer Feign Clients

```java
@SpringBootApplication
@EnableFeignClients
public class VotreApplication {
    public static void main(String[] args) {
        SpringApplication.run(VotreApplication.class, args);
    }
}
```

#### 4. Configurer l'URL du service Camunda

Dans `application.yml` de votre microservice :

```yaml
camunda:
  service:
    url: http://localhost:8085
```

#### 5. Utiliser le client Feign

```java
@Service
public class InscriptionService {
    
    @Autowired
    private CamundaFeignClient camundaClient;
    
    public void demarrerProcessusInscription(String studentId, String studentName) {
        StartProcessRequest request = new StartProcessRequest();
        request.setProcessDefinitionKey("inscription-process");
        request.setBusinessKey("INS-" + studentId);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("studentId", studentId);
        variables.put("studentName", studentName);
        variables.put("assignee", "admin");
        request.setVariables(variables);
        
        ResponseEntity<Map<String, Object>> response = camundaClient.startProcess(request);
        System.out.println("Process started: " + response.getBody().get("processInstanceId"));
    }
    
    public List<TaskDto> getMesTaches(String userId) {
        ResponseEntity<List<TaskDto>> response = camundaClient.getUserTasks(userId);
        return response.getBody();
    }
    
    public void completerTache(String taskId, boolean approved) {
        CompleteTaskRequest request = new CompleteTaskRequest();
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approved);
        request.setVariables(variables);
        
        camundaClient.completeTask(taskId, request);
    }
}
```

## 🖥 Camunda Webapp

### Accès aux applications web Camunda

Une fois le service démarré, accédez aux applications web :

#### 1. Camunda Cockpit (Monitoring)
- **URL**: http://localhost:8085/camunda/app/cockpit/default/
- **Fonction**: Surveiller les instances de processus, voir les statistiques

#### 2. Camunda Tasklist (Gestion des tâches)
- **URL**: http://localhost:8085/camunda/app/tasklist/default/
- **Fonction**: Gérer et compléter les tâches utilisateur

#### 3. Camunda Admin (Administration)
- **URL**: http://localhost:8085/camunda/app/admin/default/
- **Fonction**: Gérer les utilisateurs, groupes, autorisations

**Credentials**: admin / admin

### REST API Camunda native

L'API REST native de Camunda est également disponible :

- **Base URL**: http://localhost:8085/engine-rest/
- **Documentation**: https://docs.camunda.org/manual/7.21/reference/rest/

Exemples :
- GET `/engine-rest/process-definition` - Liste des définitions de processus
- GET `/engine-rest/task` - Liste des tâches
- POST `/engine-rest/process-definition/key/{key}/start` - Démarrer un processus

## 🧪 Tests

### Test 1: Vérifier que le service démarre

```bash
curl http://localhost:8085/actuator/health
```

**Réponse attendue**:
```json
{
  "status": "UP"
}
```

### Test 2: Démarrer un processus d'inscription

```bash
curl -X POST http://localhost:8085/api/process/start \
  -H "Content-Type: application/json" \
  -d '{
    "processDefinitionKey": "inscription-process",
    "businessKey": "INS-2026-001",
    "variables": {
      "studentId": "12345",
      "studentName": "Jean Dupont",
      "assignee": "admin"
    }
  }'
```

### Test 3: Récupérer les tâches de l'admin

```bash
curl http://localhost:8085/api/tasks/user/admin
```

### Test 4: Compléter une tâche

```bash
curl -X POST http://localhost:8085/api/tasks/{taskId}/complete \
  -H "Content-Type: application/json" \
  -d '{
    "variables": {
      "approved": true
    }
  }'
```

### Test 5: Vérifier dans Camunda Cockpit

1. Ouvrir http://localhost:8085/camunda/app/cockpit/default/
2. Se connecter avec admin/admin
3. Cliquer sur "Processes" → "inscription-process"
4. Vérifier que l'instance de processus apparaît

### Test 6: Gérer les tâches dans Tasklist

1. Ouvrir http://localhost:8085/camunda/app/tasklist/default/
2. Se connecter avec admin/admin
3. Voir la tâche "Vérifier les données"
4. Compléter la tâche avec les variables nécessaires

## 📝 Processus BPMN inclus

Le projet inclut un processus d'exemple : **inscription-process**

### Étapes du processus :
1. **Début inscription** (Start Event)
2. **Vérifier les données** (User Task)
3. **Gateway de décision** : Données valides ?
   - Si **Oui** → Approuver l'inscription
   - Si **Non** → Rejeter l'inscription
4. **Fin** (End Events)

### Variables du processus :
- `studentId` : ID de l'étudiant
- `studentName` : Nom de l'étudiant
- `assignee` : Utilisateur assigné aux tâches
- `approved` : Boolean pour la décision (true/false)

## 🔧 Dépannage

### Erreur de connexion MySQL

**Problème**: `Communications link failure`

**Solution**: Vérifiez que MySQL est démarré et que les credentials sont corrects.

### Port 8085 déjà utilisé

**Solution**: Modifier le port dans `application.yml` :
```yaml
server:
  port: 8086  # Nouveau port
```

### Processus BPMN non déployé

**Solution**: Vérifiez que le fichier `.bpmn` est dans `src/main/resources/processes/`

## 📚 Ressources

- [Documentation Camunda 7.21](https://docs.camunda.org/manual/7.21/)
- [Camunda REST API](https://docs.camunda.org/manual/7.21/reference/rest/)
- [Spring Boot 2.7 Documentation](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/)
- [OpenFeign Documentation](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)

## 📄 Licence

Ce projet est un exemple pour une architecture microservices éducative.
