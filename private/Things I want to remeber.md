## Hyundai Internship

### Predictive Maintenance System

- Developed a dual-branch LSTM model for conveyor machine wear-and-tear prediction using one year of sensor data (current, voltage, resistance, cycle count).
- Replaced legacy DTW-based pattern matching with deep learning-based temporal analysis for better scalability and faster inference.
- Built architecture using LSTM, Dense, and Dropout layers to detect operational degradation across repetitive industrial cycles.
- Benchmarked performance using accuracy, RMSE, inference latency, and scalability comparisons against DTW.

### Checklist Management System Optimization

- Redesigned inefficient checklist-specific database structures into a centralized metadata-driven relational schema supporting 2300+ checklists.
- Applied normalization, indexed relational mapping, and reusable attribute tables to reduce redundancy and improve query performance.
- Integrated Redis caching for frequently accessed checklist data to reduce database load and improve response time.
- Designed the architecture for future scalability and horizontal partitioning support.