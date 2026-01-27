**Block Storage**: Like a hard drive attached to your computer. Used for databases and EC2 boot volumes. Example: Amazon EBS.

[[Block storage]]

**File Storage**: Like a shared network folder with directories. Multiple users can access at once. Example: Amazon EFS . Have metadatas

[[File Storage]]

**Object Storage**: Stores files as objects with metadata, accessed via web APIs. Best for backups, media files, and large-scale data. Example: Amazon S3.

[[Object Storage]]

---

**Amazon FSx** is a fully managed service that provides high-performance file systems optimized for specific workloads and applications. It offers native compatibility with popular file systems, making it easy to migrate existing applications to AWS without code changes.

**Benefits:**

- Fully managed infrastructure with automatic patching, backups, and maintenance

- High performance with sub-millisecond latencies and high throughput

- Native compatibility with existing applications and tools

- Built-in data protection with automated backups and replication

- Scalable storage and performance that can grow with your needs

**Use Cases:**

- Windows-based applications requiring SMB protocol support

- High-performance computing (HPC) and machine learning workloads

- Media processing and content management

- Enterprise applications like databases and business analytics

- Home directories and file shares for organizations

**Available File System Options:**

- **Amazon FSx for Windows File Server**: Provides fully managed Windows native file storage with SMB protocol support, Active Directory integration, and Windows NTFS file system features. Ideal for Windows applications, SQL Server databases, and enterprise workloads.

- **Amazon FSx for Lustre**: _(Extremely fast)_ High-performance file system optimized for compute-intensive workloads like machine learning, HPC, video processing, and financial modeling. Can process massive datasets at speeds up to hundreds of gigabytes per second.

- **Amazon FSx for NetApp ONTAP**: Delivers NetApp's data management capabilities with support for NFS, SMB, and iSCSI protocols. Provides advanced features like snapshots, cloning, and data tiering.

- **Amazon FSx for OpenZFS**: _(Data Integerrity)_ Cost-effective file storage built on the OpenZFS file system, offering high performance for Linux workloads with features like snapshots, data compression, and point-in-time cloning.

---

**AWS Storage Gateway** is a hybrid cloud storage service that makes it possible to seamlessly integrate on-premises environments with AWS Cloud storage. You can use it to extend your local storage

- **Amazon S3 File Gateway** bridges your local environment with Amazon S3.

- **Volume Gateway** create virtual storage volumes while maintaining local access to your data.
    
    - _Cached volume mode_ stores primary data in the cloud while frequently accessed data is cached locally for low-latency access.
    
    - _Stored volume mode_ locally keeps your complete dataset while asynchronously backing it up to the cloud as EBS snapshots.
    

- **Tape Gateway** makes it possible to replace physical tape infrastructure with virtual tape capabilities

---

**Elastic Disaster Recovery** replicates critical workloads to AWS with minimal downtime. Your servers' block-level data is continuously replicated to AWS