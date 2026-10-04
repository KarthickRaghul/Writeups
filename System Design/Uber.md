![](https://www.systemdesignhandbook.com/wp-content/uploads/2026/02/uber_system_overview.svg)

**Functional requirements**:
- Riders need to request trips by entering pickup and drop-off locations, then get matched with nearby available drivers. 
- Both parties must track each other’s location in real time throughout the journey.
- The system must calculate fares based on distance, time, and demand conditions, then process payments securely.
- A ratings system allows riders and drivers to evaluate each other after each trip, maintaining platform quality.

**Non-functional requirements:** 
- Scalability means supporting millions of concurrent users across multiple continents. 
- Low latency demands that matching happen within seconds, not minutes. 
- High availability ensures the service remains operational during peak demand periods like New Year’s Eve or major sporting events. 
- Fault tolerance keeps the system running even when individual components fail. In a system this complex, something is always failing somewhere.

---
### High-level architecture and core components

![](https://www.systemdesignhandbook.com/wp-content/uploads/2026/02/ride_request_flow.svg)


---
 > say about user and driver management after this -> the tables , the details 
 ---
### Real-time location tracking at scale

| Approach     | What it basically does                          | Good for                         |
| ------------ | ----------------------------------------------- | -------------------------------- |
| **Geohash**  | Converts location → string/grid cell            | Simple proximity searches        |
| **Quadtree** | Recursively splits space into 4                 | Variable-density point data      |
| **H3**       | Converts location → hierarchical hexagonal cell | Geographic analytics/aggregation |
| **R-tree**   | Organizes spatial objects using bounding boxes  | Complex GIS queries              |

**Hot Location Store:**
- Stores **latest location** of active drivers.
- Needs **very fast reads/writes**.
- **Redis GEO** is commonly suitable.
- Used for **nearby-driver search and ride matching**.

**Cold / Historical Store:**
- Stores **complete location history**.
- Used for:
    - Trip reconstruction
    - Analytics
    - Compliance
- Examples: **Parquet + object storage**, time-series DB.
- Optimized for **storage efficiency**, not ultra-low latency.

**Dead Reckoning:**
- Predicts position between GPS updates using:
    - Last position
    - Speed
    - Heading
- Makes vehicle movement appear **smooth**.

**Scaling:**
- **Consistent hashing** → distributes drivers across multiple servers.
- **Gossip protocol** → nodes share membership/failure information.
- Enables **horizontal scaling**.

---
### The matching algorithm

![](https://www.systemdesignhandbook.com/wp-content/uploads/2026/02/matching_algorithm_flow.svg)

---
### Ride lifecycle management

Different states :
- Requested
- Accepted
- Driver arriving
- In Progress
- Completed
- Paid
- Rated

---
### Payment processing and fare calculation

**Fare calculation** : final_fare = (base + (distance × rate_per_km) + (time × rate_per_minute)) × surge_multiplier – promotions.
**Idempotency** is critical for payment operations.By including an idempotency key with every charge request, you can safely retry without risking double charges

---
### Notifications and real-time communication

![](https://www.systemdesignhandbook.com/wp-content/uploads/2026/02/notification_architecture.svg)

---

