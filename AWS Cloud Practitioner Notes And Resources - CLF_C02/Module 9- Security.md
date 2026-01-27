_Authentication_ is the process of verifying the identity of a user or entity through credentials like a username and password combination.

_Authorization_ grants users certain access rights and permissions that determine which actions they can perform in a system or application.

---

IAM - Identity and Access Management : Securely manage identities and access to AWS services and resources.

**User:** Individual accounts with unique credentials and permissions

**Group:** Collection of users that share common access permissions

**Roles:** Temporary credentials that can be assumed by users or services to perform specific tasks

  

**AWS IAM Identity Center**: Centrally manages workforce access to multiple AWS accounts and applications with single sign-on (SSO).

**AWS Secrets Manager**: Securely stores, manages, and rotates credentials like database passwords and API keys.

**AWS Systems Manager**: Provides operational tools to manage and automate tasks across AWS resources and on-premises systems.

---

Elastic Load Balancing and Security groups are important to mitigate Ddos attacks

**AWS Shield** is designed to automatically protect AWS customers from the most common, frequently occurring types of DDoS attacks at no cost

_AWS Shield Advanced_ is a paid service that provides detailed attack diagnostics and the ability to detect and mitigate sophisticated DDoS attacks. It also integrates with other services, such as Amazon CloudFront, Amazon Route 53, and ELB.

**AWS WAF** is a web application firewall that monitors network requests that come into your web applications.

---

**AWS Key Management Service (AWS KMS)**: Creates and manages encryption keys to secure your data across AWS services.

**Amazon Macie**: Uses machine learning to automatically discover, classify, and protect sensitive data like personal information in AWS.

**AWS Certificate Manager (ACM)**: Provisions, manages, and deploys SSL/TLS certificates for securing network communications.

---

**Amazon Inspector**: Automated vulnerability management service that continuously **scans workloads for software vulnerabilities** and network exposure. It focuses on assessing the security posture of your applications and instances.

**Amazon GuardDuty**: Threat detection service that **monitors** for malicious activity and unauthorized behavior. It analyzes logs and network traffic to identify potential security threats in real-time.

**Amazon Detective**: Security investigation service that helps analyze and visualize security data to investigate potential issues. **It's used after threats are detected** (often by GuardDuty) to conduct root cause analysis.

**AWS Security Hub**: Centralized security management service that aggregates, organizes, and prioritizes security findings from multiple AWS services and third-party tools. It provides a comprehensive view of your security posture across your AWS accounts.

**Key Distinction**: Inspector scans for vulnerabilities, GuardDuty detects threats, Detective investigates incidents, and Security Hub consolidates all security findings in one place.

---