**Amazon Virtual Private Cloud (Amazon VPC) -** Amazon VPC lets you provision a logically isolated section of the AWS cloud where you can launch AWS resources in a virtual network that you define.

---

AWS Cloud → Region → Amazon VPC →Availablity Zones → Subnet

---

**Amazon Virtual Private Cloud** is used to establish boundaries around your AWS resources.

**Virtual private gateway** allows protected internet traffic to enter into the VPC.

**Virtual private network** encrypts your internet traffic, helping protect it from anyone who might try to intercept or monitor it.

---

**Four ways to connect to the AWS Cloud:**

- **AWS Client VPN** – Allows individual users to securely connect to AWS from their personal devices over the internet.

- **AWS Site-to-Site VPN** – Securely connects an on-premises office network to AWS using an encrypted tunnel over the internet. Companies , employees

- **AWS PrivateLink** – Enables private communication between AWS services without using the public internet.

- **AWS Direct Connect** – Provides a dedicated physical network connection from a data center directly to AWS for high speed and reliability. - Latency sensititve applications - large scale data migration - hybrid cloud architechture

- Amazon Connect

---

**Other Gateways :**

- **AWS Transit Gateway** – A central hub that connects multiple VPCs and on-premises networks together.

- **NAT (Network Address Translation) Gateway** – Allows private instances to access the internet **outbound only**, without allowing inbound internet traffic.

- **Amazon API Gateway** – A service to create, manage, and secure APIs that connect clients to backend services.

---

![[image 1.png]]

Network ACL - Statefull

Security Groups - Stateless

---

DNS - Route 53

**Amazon CloudFront -** is a content delivery network (CDN) service that delivers your content with faster loading times, cost savings, and reliability.

**Application Load Balancer -** Amazon CloudFront connects to the Application Load Balancer, which sends the incoming packet to an Amazon EC2 instance.

**AWS Global Accelerator -** Global Accelerator is a service that uses the AWS global network to improve application availability, performance, and security.