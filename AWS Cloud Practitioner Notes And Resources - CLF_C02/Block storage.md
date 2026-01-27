**Amazon EC2 Instance Store**

EC2 instance store provides temporary block-level storage that is physically attached to the host computer. This storage is ideal for temporary data that changes frequently, such as buffers, caches, scratch data, and other temporary content.

**Amazon EBS (Elastic Block Store)**

EBS provides persistent block-level storage volumes that can be attached to EC2 instances. The data persists independently from the life of the instance.Amazon EBS provides high availability and durability by automatically replicating volumes within the same Availability Zone.Amazon EBS volumes exist independently from the instance and persist even after the instance is terminated.

  

**EBS Snapshots** are point-in-time backups of your Amazon EBS volumes. They capture the entire state of a volume at a specific moment and are stored in Amazon S3 for durability.EBS snapshots are incremental

**Amazon Data Lifecycle Manager**

- Schedule automatic snapshot creation

- Set retention policies

- Manage snapshot lifecycle

- Apply consistent backup policies