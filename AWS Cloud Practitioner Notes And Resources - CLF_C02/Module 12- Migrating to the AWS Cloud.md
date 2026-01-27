**Three phases of the migration process :** Asses , Mobilize , Migrate

**AWS Cloud Adoption FrameworkCAF** to migrate to the AWS Cloud.

> [!info] AWS Cloud Adoption Framework  
> The AWS Cloud Adoption Framework helps enterprises effectively adopt the AWS cloud  
> [https://aws.amazon.com/cloud-adoption-framework/](https://aws.amazon.com/cloud-adoption-framework/)  

- **Business perspective**: Ensures IT aligns with business needs and that IT investments link to key business results.

- **People perspective**: Supports development of an organization-wide change management strategy for successful cloud adoption.

- **Governance perspective**: Focuses on how to update the staff skills and processes necessary to maintain business governance in the cloud.

- **Platform perspective**: Includes principles and patterns for implementing new solutions in the cloud and migrating on-premises workloads to the cloud.

- **Security perspective**: Ensures that the organization meets security objectives for visibility, auditability, control, and agility.

- **Operations perspective**: Helps you to enable, run, use, operate, and recover IT workloads to the level agreed upon with your business stakeholders.

---

The seven migration strategies (also known as the "7 Rs") are:

- **Relocating**: Moving applications or infrastructure to the cloud with minimal changes, often used for moving entire data centers or large-scale infrastructure.

- **Rehosting (Lift and Shift)**: Moving applications to the cloud without making any changes. This is the quickest migration approach.

- **Replatforming (Lift, Tinker, and Shift)**: Moving applications to the cloud with some cloud optimizations, but without changing the core architecture.

- **Refactoring/Re-architecting**: Redesigning applications to take full advantage of cloud-native features and capabilities.

- **Repurchasing**: Moving to a different product, typically by switching to a SaaS platform (e.g., migrating from a traditional CRM to Salesforce).

- **Retaining**: Keeping certain applications in their current environment, either because they're not ready to migrate or there's no business case for migration.

- **Retiring**: Decommissioning applications that are no longer needed or useful.

---

**Migration Evaluator**: Provides data-driven insights to help build a business case for migration by analyzing your on-premises infrastructure and creating cost projections for running workloads in AWS.

**Application Discovery Service**: Automatically discovers and collects information about your on-premises servers, applications, and dependencies to help plan your migration.

**Migration Hub** is a centralized hub that takes the company through the discovery, assessment, planning, and implementation phases of migration. **no charge to use Migration Hub.**

**Application Migration Service**: Automates the lift-and-shift migration process by replicating source servers to AWS and converting them to run natively in the cloud with minimal downtime.

---

**AWS Database Migration Service DMS** makes it possible to quickly and securely migrate databases and perform ongoing data replication tasks . used to plan, assess, convert, and migrate the database.

**AWS Schema Conversion Tool SCT** makes it possible to convert database schemas and code objects (like stored procedures, views, and functions) from one database engine to another.

---

**AWS DataSync**: A data transfer service that automates moving large amounts of data between on-premises storage and AWS storage services (like S3, EFS) or between AWS services. It handles encryption, data validation, and network optimization.

**AWS Transfer Family**: A fully managed service that enables secure file transfers directly into and out of AWS storage services using standard protocols like SFTP, FTPS, and FTP. Useful for legacy applications that rely on these protocols.

**Direct Connect** is a service that makes it possible for you to establish a dedicated private connection between your network and virtual private cloud (VPC)

**Snowball Edge Storage Optimized devices** deliver high performance NVMe storage, making it possible to simplify multi-petabyte data migrations from on-premises locations to AWS.