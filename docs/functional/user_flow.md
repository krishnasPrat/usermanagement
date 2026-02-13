# User Flow (Pictorial)

## Access Flow

```mermaid
flowchart LR
  A["Owner User (Umesh)"] --> B["Subscribe to Service"]
  B --> C[Subscription]
  C --> D[Add Sub-User]
  D --> E[Assign Role]
  E --> F[Default Role Permissions]
  D --> G["Special Permissions (Overrides)"]
  F --> H[Effective Permissions]
  G --> H
  H --> I[Service Actions Allowed]
```

## Visual 1: Super User Subscribes to Services

```mermaid
flowchart LR
  A["AWS Console"] --> B["Service Catalog"]
  B --> C["EC2"]
  B --> D["Lambda"]
  B --> E["Cassandra"]

  U["Super User: Umesh"] --> S["Subscriptions"]
  C --> S
  D --> S
  E --> S

  S --> X["Active Subscriptions:\nEC2, Lambda, Cassandra"]
```

## Visual 2: Super User Adds Team to Subscriptions

```mermaid
flowchart TB
  U["Super User: Umesh"] --> SUB["Subscriptions"]

  SUB --> EC2["EC2 Subscription"]
  SUB --> CASS["Cassandra Subscription"]

  EC2 --> SH["Shyam (Admin Role)"]
  EC2 --> RA["Raju (User Role)"]

  CASS --> SH2["Shyam (User Role)"]

  RA --> SP["Special Permissions:\nSTART_INSTANCE, STOP_INSTANCE"]
```

## Example (Split) - EC2

```mermaid
flowchart TB
  subgraph EC2["Service: EC2"]
    ec2_admin["Role: ADMIN"]
    ec2_user["Role: USER"]
    ec2_start["Permission: START_INSTANCE"]
    ec2_stop["Permission: STOP_INSTANCE"]
    ec2_sudo["Permission: SUDO_ACCESS"]
    ec2_admin --> ec2_start
    ec2_admin --> ec2_stop
    ec2_admin --> ec2_sudo
    ec2_user --> ec2_start
    ec2_user --> ec2_stop
  end

  umesh["Umesh (Owner)"] --> sub_ec2["Subscription: EC2"]
  sub_ec2 --> shyam["Shyam"]
  sub_ec2 --> raju["Raju"]

  shyam --> ec2_admin
  raju --> ec2_user
  raju --> sp_start["Special: START_INSTANCE"]
  raju --> sp_stop["Special: STOP_INSTANCE"]
```

## Example (Split) - Cassandra

```mermaid
flowchart TB
  subgraph CASS["Service: Cassandra"]
    cass_admin["Role: ADMIN"]
    cass_user["Role: USER"]
    cass_create_schema["Permission: CREATE_SCHEMA"]
    cass_drop_schema["Permission: DROP_SCHEMA"]
    cass_create_table["Permission: CREATE_TABLE"]
    cass_drop_table["Permission: DROP_TABLE"]
    cass_dml["Permission: DML"]
    cass_dql["Permission: DQL"]
    cass_admin --> cass_create_schema
    cass_admin --> cass_drop_schema
    cass_admin --> cass_create_table
    cass_admin --> cass_drop_table
    cass_admin --> cass_dml
    cass_admin --> cass_dql
    cass_user --> cass_dml
    cass_user --> cass_dql
  end

  umesh["Umesh (Owner)"] --> sub_cass["Subscription: Cassandra"]
  sub_cass --> shyam["Shyam"]

  shyam --> cass_user
```

## Example (Umesh, Shyam, Raju) - Full (Top to Bottom)

```mermaid
flowchart TB
  subgraph Services["Services Subscribed by Umesh"]
    subgraph EC2["Service: EC2"]
      ec2_admin["Role: ADMIN"]
      ec2_user["Role: USER"]
      ec2_start["Permission: START_INSTANCE"]
      ec2_stop["Permission: STOP_INSTANCE"]
      ec2_sudo["Permission: SUDO_ACCESS"]
      ec2_admin --> ec2_start
      ec2_admin --> ec2_stop
      ec2_admin --> ec2_sudo
      ec2_user --> ec2_start
      ec2_user --> ec2_stop
    end

    subgraph CASS["Service: Cassandra"]
      cass_admin["Role: ADMIN"]
      cass_user["Role: USER"]
      cass_create_schema["Permission: CREATE_SCHEMA"]
      cass_drop_schema["Permission: DROP_SCHEMA"]
      cass_create_table["Permission: CREATE_TABLE"]
      cass_drop_table["Permission: DROP_TABLE"]
      cass_dml["Permission: DML"]
      cass_dql["Permission: DQL"]
      cass_admin --> cass_create_schema
      cass_admin --> cass_drop_schema
      cass_admin --> cass_create_table
      cass_admin --> cass_drop_table
      cass_admin --> cass_dml
      cass_admin --> cass_dql
      cass_user --> cass_dml
      cass_user --> cass_dql
    end
  end

  umesh["Umesh (Owner)"] --> sub_ec2["Subscription: EC2"]
  umesh --> sub_cass["Subscription: Cassandra"]

  sub_ec2 --> shyam["Shyam"]
  sub_ec2 --> raju["Raju"]
  sub_cass --> shyam

  shyam --> ec2_admin
  shyam --> cass_user

  raju --> ec2_user
  raju --> sp_start["Special: START_INSTANCE"]
  raju --> sp_stop["Special: STOP_INSTANCE"]
```
