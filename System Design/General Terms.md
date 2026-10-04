> [!Note]
> Never store raw credit card numbers in your databases. Always use tokenization through payment providers like Stripe or Braintree, which return secure tokens you can charge later without handling sensitive card data directly.

**Service Mesh:** A service mesh is a dedicated infrastructure layer that handles communication between microservices in a software application. It provides a set of tools and capabilities to manage, secure, and monitor the interactions between different services
*Eg : Istio, Linkerd, AWS App Mesh*


**P99 latency:** P99 latency is the 99th percentile of response times in a system, meaning that 99% of requests finish in that amount of time or faster, while the slowest 1% take longer.

**Idempotency** means that performing an action multiple times produces the same result as performing it just once