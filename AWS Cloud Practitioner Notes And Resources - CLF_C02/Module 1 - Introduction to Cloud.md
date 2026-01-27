
AWS only ask payment for what you use

You can deploy cloud resources in multiple ways: cloud, on-premises, and hybrid.

**Cloud Based Deployment** : migrate your existing resources to the cloud, design and build new applications within the cloud

**On-Premise Deployment** : Deploying resources on premises using virtualization and resource management tools does not provide many of the benefits of cloud computing. However, it is sometimes sought for its ability to provide dedicated resources and low latency. In most cases this deployment model is the same as legacy IT infrastructure while using application management and virtualization technologies to try increasing resource utilization.

**Hybrid Deployment :** cloud-based resources and on-premises infrastructure work together. Mainly for legacy applications

### Six Benefits of AWS:

- Trade fixed expenses for variable expenses
- Benefit from massive economies of scale
- Stop guessing capacity
- Increase speed and agility
- Stop spending money running and maintaining data centers
- Go global in minutes

---

**AWS Regions and Availability Zones**

AWS Regions are separate geographic areas where AWS has data centers. Each Region is completely independent and isolated from other Regions.

Availability Zones (AZs) are one or more discrete data centers within a Region, each with redundant power, networking, and connectivity. Each Region has multiple Availability Zones (typically 3 or more) that are physically separated but connected through low-latency links.

**High Availability**

High availability means your applications and services remain operational and accessible even when failures occur. By deploying resources across multiple Availability Zones within a Region, you ensure that if one AZ experiences an outage, your application continues running in the other AZs.

**Fault Tolerance**

Fault tolerance is the ability of a system to continue operating properly even when some of its components fail. AWS achieves this through:

- Redundant infrastructure across multiple AZs
- Automatic failover mechanisms
- Data replication across zones
- Load balancing to distribute traffic across healthy resources

---

**AWS Shared Responsibility Model**

The AWS Shared Responsibility Model divides security and compliance responsibilities between AWS and the customer:

**AWS Responsibilities ("Security OF the Cloud")**

- Physical infrastructure and hardware
- Network infrastructure and virtualization layer
- Managed services infrastructure
- Global infrastructure (Regions, Availability Zones, Edge Locations)
- Physical security of data centers

**Customer Responsibilities ("Security IN the Cloud")**

- Customer data and encryption
- Operating systems, network, and firewall configuration
- Identity and access management (IAM)
- Application code and data security
- Network traffic protection
- Server-side and client-side encryption

**Shared Responsibilities**

- Patch management: AWS patches infrastructure, customers patch guest OS and applications
- Configuration management: Both parties manage their respective layers
- Awareness and training: Both ensure their teams understand security best practices