# Schema Diagrams

## Simple Flow Diagram

```mermaid
flowchart LR
  U[User] --> S[Subscription]
  S --> SV[Service]

  SV --> R[Role]
  SV --> P[Permission]

  R --> SRP[ServiceRolePermissions]
  P --> SRP

  S --> SU[SubscriptionUser]
  U --> SU
  R --> SU

  SU --> SP[SpecialPermissions]
  P --> SP
```

## EER Diagram

```mermaid
erDiagram
  USERS {
    uuid id PK
    string name
    string email
  }

  SERVICES {
    uuid id PK
    string name
  }

  ROLES {
    uuid id PK
    uuid service_id FK
    string name
  }

  PERMISSIONS {
    uuid id PK
    uuid service_id FK
    string name
  }

  SERVICE_ROLE_PERMISSIONS {
    uuid service_id FK
    uuid role_id FK
    uuid permission_id FK
  }

  SUBSCRIPTIONS {
    uuid id PK
    uuid owner_user_id FK
    uuid service_id FK
  }

  SUBSCRIPTION_USERS {
    uuid subscription_id FK
    uuid user_id FK
    uuid role_id FK
  }

  SPECIAL_PERMISSIONS {
    uuid subscription_user_id FK
    uuid permission_id FK
  }

  USERS ||--o{ SUBSCRIPTIONS : owns
  SERVICES ||--o{ SUBSCRIPTIONS : subscribed_to

  SERVICES ||--o{ ROLES : defines
  SERVICES ||--o{ PERMISSIONS : defines

  ROLES ||--o{ SERVICE_ROLE_PERMISSIONS : default_grants
  PERMISSIONS ||--o{ SERVICE_ROLE_PERMISSIONS : granted_by

  SUBSCRIPTIONS ||--o{ SUBSCRIPTION_USERS : assigns
  USERS ||--o{ SUBSCRIPTION_USERS : member
  ROLES ||--o{ SUBSCRIPTION_USERS : role_per_service

  SUBSCRIPTION_USERS ||--o{ SPECIAL_PERMISSIONS : exceptions
  PERMISSIONS ||--o{ SPECIAL_PERMISSIONS : granted_direct
```

## Sample Data (Illustrative)

**Users**

| id | name | email | title |
| --- | --- | --- | --- |
| u_krishna | Krishna | krishna@company.com | CTO |
| u_shyam | Shyam | shyam@company.com | Team Lead |
| u_raju | Raju | raju@company.com | Junior Dev |

**Services**

| id | name |
| --- | --- |
| svc_ec2 | EC2 |
| svc_lambda | Lambda |
| svc_cassandra | Cassandra |

**Roles**

| id | service_id | name |
| --- | --- | --- |
| role_ec2_admin | svc_ec2 | ADMIN |
| role_ec2_user | svc_ec2 | USER |
| role_cass_admin | svc_cassandra | ADMIN |
| role_cass_user | svc_cassandra | USER |

**Permissions**

| id | service_id | name |
| --- | --- | --- |
| perm_ec2_start | svc_ec2 | start_instance |
| perm_ec2_stop | svc_ec2 | stop_instance |
| perm_ec2_sudo | svc_ec2 | sudo_access |
| perm_cass_create_schema | svc_cassandra | create_schema |
| perm_cass_drop_schema | svc_cassandra | drop_schema |
| perm_cass_create_table | svc_cassandra | create_table |
| perm_cass_drop_table | svc_cassandra | drop_table |
| perm_cass_dml | svc_cassandra | DML |
| perm_cass_dql | svc_cassandra | DQL |

**Service Role Permissions (defaults)**

| service_id | role_id | permission_id |
| --- | --- | --- |
| svc_ec2 | role_ec2_admin | perm_ec2_start |
| svc_ec2 | role_ec2_admin | perm_ec2_stop |
| svc_ec2 | role_ec2_admin | perm_ec2_sudo |
| svc_ec2 | role_ec2_user | perm_ec2_start |
| svc_ec2 | role_ec2_user | perm_ec2_stop |
| svc_cassandra | role_cass_admin | perm_cass_create_schema |
| svc_cassandra | role_cass_admin | perm_cass_drop_schema |
| svc_cassandra | role_cass_admin | perm_cass_create_table |
| svc_cassandra | role_cass_admin | perm_cass_drop_table |
| svc_cassandra | role_cass_admin | perm_cass_dml |
| svc_cassandra | role_cass_admin | perm_cass_dql |
| svc_cassandra | role_cass_user | perm_cass_dml |
| svc_cassandra | role_cass_user | perm_cass_dql |

**Subscriptions**

| id | owner_user_id | service_id |
| --- | --- | --- |
| sub_ec2_krishna | u_krishna | svc_ec2 |
| sub_cass_krishna | u_krishna | svc_cassandra |

**Subscription Users**

| id | subscription_id | user_id | role_id |
| --- | --- | --- | --- |
| subu_shyam_ec2 | sub_ec2_krishna | u_shyam | role_ec2_admin |
| subu_shyam_cass | sub_cass_krishna | u_shyam | role_cass_user |
| subu_raju_ec2 | sub_ec2_krishna | u_raju | role_ec2_user |

**Special Permissions**

| subscription_user_id | permission_id |
| --- | --- |
| subu_raju_ec2 | perm_ec2_start |
| subu_raju_ec2 | perm_ec2_stop |
