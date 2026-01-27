**Amazon Simple Storage Service (S3)** stores files as objects in containers known as buckets upto a size of 5 TB (object size) . When you upload a file to Amazon S3, it becomes an object and is stored durably across multiple facilities within your chosen Region.Each object typically includes the _data_ itself, _metadata_, and a unique identifier, or _key_. Version control also possible here . For public objects and buckets Amazon S3 data to be available to everyone on the internet.

- Amazon S3 bucket policies are _resource-based policies_ that can only be attached to S3 buckets.

- Permissions that control what actions users, groups, or roles can perform on S3 resources are configured using _identity-based policies_.

- Amazon S3 provides encryption capabilities to protect data both at rest and in transit.

  

**Amazon S3 Storage Classes**

- **S3 Standard**: For frequently accessed data with low latency and high throughput. Ideal for active workloads, content distribution, and data analytics.

- **S3 Standard-IA (Infrequent Access)**: For data accessed less frequently but requiring rapid access when needed. Lower storage cost but charges for retrieval.

- **S3 One Zone-IA**: Similar to Standard-IA but stores data in a single Availability Zone, offering lower cost with reduced redundancy.

- **S3 Intelligent-Tiering**: Automatically moves objects between access tiers based on usage patterns, optimizing costs without performance impact or operational overhead.

- **S3 Glacier Instant Retrieval**: For archive data that needs immediate access, with retrieval in milliseconds.cost savings of up to 68 percent compared to the S3 Standard-IA storage class

- **S3 Glacier Flexible Retrieval**: For archive data with retrieval times ranging from minutes to hours, offering significant cost savings.

- **S3 Glacier Deep Archive**: Lowest-cost storage for long-term archive and digital preservation, with retrieval times of 12-48 hours.

- **S3 Outposts:** Amazon S3 Outposts delivers object storage to your on-premises AWS Outposts environment using Amazon S3 APIs and features, and serves workloads with local data residency requirements

  

**S3 Lifecycle**

To avoid manually managing your object storage tier configurations, you can use S3 Lifecycle configurations to automate the process

- _Transition actions:_ define when objects should transition to another storage class.

- _Expiration actions:_ define when objects expire and should be permanently deleted.