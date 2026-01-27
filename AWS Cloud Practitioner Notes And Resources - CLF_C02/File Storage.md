**Amazon EFS (Elastic File Storage)**

Amazon EFS is a fully managed file storage service that provides scalable, elastic file storage for use with AWS services and on-premises resources. It implements the NFSv4 protocol, allowing multiple EC2 instances to access the same file system concurrently.

**Key Benefits:**

- Scalability: Automatically grows and shrinks as you add and remove files, with no need for provisioning

- High availability and durability: Data is stored redundantly across multiple Availability Zones

- Shared access: Multiple instances can access the file system simultaneously

- Performance modes: Choose between General Purpose (latency-sensitive) and Max I/O (higher aggregate throughput)

  

|   |   |   |
|---|---|---|
|**Feature**|**Amazon EBS**|**Amazon EFS**|
|**Storage Type**|Block storage (like a hard drive)|True file system for Linux|
|**Availability Zone**|AZ-level resource; must be in the same AZ as EC2 instance|Not AZ-limited; accessible throughout the region or from other regions|
|**Concurrent Access**|Attached to a single EC2 instance|Multiple instances can read/write simultaneously|
|**Scaling**|Manual; does not automatically scale when full|Automatic; scales up and down as needed|
|**Use Cases**|Save files, run databases, store applications|Shared file systems across applications, analytics on shared data|
|**Lifecycle Management**|Manual management|Pre-configured lifecycle policies with automatic data movement between storage classes|

**EFS Storage Classes**

EFS offers multiple storage classes to optimize costs:

- **Standard**: For frequently accessed data, stored across multiple Availability Zones

- **Infrequent Access (IA)**: Lower-cost storage for files not accessed regularly, with retrieval fees when accessed

- **One Zone**: Stores data in a single Availability Zone at reduced cost

- **One Zone-IA**: Combines single-zone storage with infrequent access pricing for maximum cost savings

**Pricing Impact:** IA storage classes can reduce storage costs by up to 92% compared to Standard storage, but charge a fee when files are accessed. One Zone classes offer additional savings (up to 47% less than Standard) but with lower redundancy.

**EFS Lifecycle Policies**

Lifecycle management automatically transitions files between storage classes based on access patterns:

- You can set policies to move files to IA storage classes after they haven't been accessed for a specified period (7, 14, 30, 60, or 90 days)

- Files are automatically moved back to Standard storage when accessed

- This automation helps optimize costs without manual intervention or application changes

- You can also set policies to transition files back to Standard storage based on access patterns

Lifecycle policies work seamlessly with storage classes to balance performance and cost—frequently accessed files remain in Standard storage for fast access, while inactive files automatically move to lower-cost IA classes.