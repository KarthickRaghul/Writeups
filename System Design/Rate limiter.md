### Functional requirements :

- 100 requests per minute per user. 
- Different API endpoints often require different limits. 
- Authentication endpoints need stricter controls than content fetching endpoints.
- These thresholds should be adjustable without code changes,
- When a request exceeds the limit, the system must return a clear error response, conventionally HTTP 429 Too Many Requests, along with headers indicating when the client can retry.

> [!tip] 
> Always include rate limit headers in your responses (X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset). Well-behaved clients use these to throttle themselves before hitting limits.

### Non Functional Requirements:

- Low latency
- Checking the limit should add minimal delay, typically under 10 milliseconds, to avoid becoming a bottleneck.
- The system must scale to support millions of users simultaneously across multiple servers and potentially multiple geographic regions.
- Fault tolerance ensures that if the rate limiter fails, it doesn’t bring down the entire system. 
- Accuracy
- Counters should remain consistent even in distributed environments where multiple servers process requests concurrently.

### Assumptions worth clarifying

- Are limits applied per user, per IP address, or globally across all traffic? 
- Do we need distributed support across multiple servers, or is single-node acceptable for this use case?
- Should limits reset at fixed intervals (every minute on the minute) or follow a sliding window model that smooths out burst behavior? 

###  Algorithms :

1. **Fixed Window Counter:** Counts requests within fixed time intervals.  
    **Eg:** Max 100 requests/min → requests from 10:00–10:01 can be 100.  
      
2. **Sliding Window Log:** Stores the timestamp of every request and checks how many occurred in the last fixed duration.  
    **Eg:** Max 100/min → at 10:00:30, count requests from 09:59:30–10:00:30.

3. **Sliding Window Counter:** Combines counts from the current and previous windows, weighted according to how much of each overlaps the sliding period.  
    **Eg:** 60 requests in previous minute + 30 in current → calculate an estimated count for the current 60-second window.
    `weighted_count = (previous_window_count × overlap_percentage) + current_window_count`

4. **Token Bucket:** Tokens are added at a fixed rate, and each request consumes a token; requests are rejected when the bucket is empty.  
    **Eg:** Bucket = 10 tokens, refill = 2/sec → initially 10 requests can burst immediately.

5. **Leaky Bucket:** Requests enter a queue and are processed at a fixed rate, smoothing out traffic spikes.  
    **Eg:** Queue holds 10 requests and processes 2/sec → extra requests are rejected when the queue is full.

| Algorithm              | Accuracy | Memory usage | Burst handling            | Best for                            |
| ---------------------- | -------- | ------------ | ------------------------- | ----------------------------------- |
| Fixed window counter   | Moderate | Very low     | Allows boundary bursts    | Simple use cases, low-stakes limits |
| Sliding window log     | Perfect  | High         | No bursts allowed         | Low-volume, high-precision needs    |
| Sliding window counter | Good     | Low          | Smoothed boundaries       | Most production API limiting        |
| Token bucket           | Good     | Very low     | Controlled bursts allowed | APIs needing burst tolerance        |
| Leaky bucket           | Good     | Low          | No bursts, queued         | Traffic shaping, outbound calls     |

### Storage

Always Redis is preferred

- **Fixed Window Counter:** Stores one request count per user per fixed time window. HashMap
- **Sliding Window Log:** Stores every request timestamp in a sorted set to count requests in the latest window. Sorted Set
- **Token Bucket:** Stores remaining tokens and the last refill time to calculate tokens lazily.**Hash** (`tokens`, `lastRefill`) or **Two Strings** (tokens + timestamp)
- **Sliding Window Counter:** Stores request counts in small time buckets and combines them to estimate the current window count. **String/Hash** for bucket counts

