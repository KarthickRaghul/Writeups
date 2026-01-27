
**EC2** - Virtual Servers Provided by AWS , When launching an EC2 instance, you start by selecting an Amazon Machine Image (AMI)

---

**Multi-tenancy in Amazon EC2:**

Multi-tenancy allows multiple virtual machines to share resources on the same physical host, with isolation between them.

---

**EC2 Instance Types and Their Characteristics:**

**General Purpose**

Use cases: Web servers, code repositories, development environments, small to medium databases

**Compute Optimized**

Use cases: Batch processing, media transcoding, high-performance web servers, scientific modeling, gaming servers, machine learning inference

**Memory Optimized**

Use cases: In-memory databases (Redis, Memcached), real-time big data analytics, high-performance databases

**Storage Optimized**

Use cases: Distributed file systems, data warehousing, high-frequency online transaction processing (OLTP) systems, log processing

**Accelerated Computing**

Use cases: Machine learning training, graphics rendering, video encoding, scientific simulations, genomics research

---

**How to interact with AWS services:**

AWS Management Console

AWS Command Line Interface (AWS CLI)

AWS SDK (Software Development Kit)

---

Amazon EC2 offers several pricing options namely

**On-Demand Instances**

Pay for compute capacity by the hour or second with no long-term commitments. Ideal for applications with unpredictable workloads, short-term projects, or when testing new applications.

**Reserved Instances**

Commit to using EC2 for a 1-year or 3-year term in exchange for significant discounts (up to 75% compared to On-Demand pricing). Best for steady-state workloads with predictable usage.

**Savings Plans**

Flexible pricing model offering lower prices in exchange for committing to a consistent amount of usage (measured in $/hour) for 1 or 3 years. Provides savings similar to Reserved Instances with more flexibility across instance families and regions.

**Spot Instances**

Purchase unused EC2 capacity at up to 90% discount compared to On-Demand prices. However, AWS can reclaim these instances with a 2-minute warning when capacity is needed. Suitable for fault-tolerant, flexible workloads like batch processing, data analysis, and background jobs.

**Dedicated Hosts**

Physical servers dedicated to your use. Useful for compliance requirements, existing server-bound software licenses, or regulatory needs that require dedicated hardware.

**Dedicated Instances**

Instances that run on hardware dedicated to a single customer, but you don't have control over the physical host like with Dedicated Hosts.

---

**Explain load balancing and elb**

**Load Balancing**

Load balancing is the process of distributing incoming network traffic across multiple servers or resources to ensure no single server becomes overwhelmed.

**Elastic Load Balancing (ELB)**

Elastic Load Balancing is AWS's fully managed load balancing service that automatically distributes incoming application traffic across multiple targets, such as EC2 instances, containers, and IP addresses.

To optimize traffic distribution, ELB uses several routing methods: Round Robin, Least Connections, IP Hash, and Least Response Time.

---

**EventBridge :** EventBridge can route events

---

**Amazon SQS :** Amazon SQS is a fully managed message queuing service that enables asynchronous communication between application components. It works by:

- Storing messages in a queue until they are processed and deleted
    
- Allowing producers to send messages without waiting for consumers to be ready
    
- Enabling consumers to retrieve and process messages at their own pace
    
- Providing reliable message delivery with automatic scaling
    

---

**Amazon Simple Notification Service (Amazon SNS)**

Amazon SNS uses a publish-subscribe (pub/sub) model where:

- Publishers send messages to topics without knowing who will receive them
    
- Subscribers register interest in specific topics
    
- When a message is published to a topic, SNS automatically delivers it to all subscribers
    
- Multiple subscribers can receive the same message simultaneously
    

---

**Tightly Coupled vs. Loosely Coupled Architectures**

**Tightly Coupled:** Components communicate directly and depend on each other being available. If one component fails, it can cause cascading failures throughout the system.

**Loosely Coupled:** Components communicate indirectly through intermediaries (like message queues). Components can operate independently, improving resilience and scalability.