# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
```txt
Granularity vs. Performance: How deep should the tracking go? Tracking cost per individual item (SKU) is accurate but computationally expensive; tracking per "Batch" or "Warehouse" is faster but less precise.

Variable vs. Fixed Costs: Labor is variable (shifts, overtime), while Warehouse rent is fixed. The system needs to distinguish between these to calculate the Cost per Order.

Indirect Costs: How do we allocate "General Overhead" (e.g., electricity, management salaries) to a specific Store?

Previous Experience: I’ve seen cases where lack of real-time labor tracking led to "invisible" costs. Integrating time-tracking data directly into the fulfillment flow is essential.
```
## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

Inventory Placement: Using the "Warehouse Replacement" logic we implemented, can we move high-velocity items to smaller, urban locations to reduce last-mile transportation costs?

Automation: Prioritizing automation in high-volume Warehouses while keeping manual processes in smaller "Micro-Fulfillment" centers.

Identification & Prioritization: Use the Pareto Principle (80/20 rule). Identify the 20% of Warehouses causing 80% of the cost overruns.

Implementation: Start with "Dark Stores" (stores used only for fulfillment) as a pilot for new optimization algorithms before rolling out to retail-hybrid locations.
```
## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
```txt
Single Source of Truth: Without integration, the "Fulfillment View" of cost and the "Accounting View" (SAP/Oracle) will inevitably diverge, leading to bad financial reporting.

Event-Driven Sync: Instead of batch uploads at night, we should use Transactional Outbox patterns (like the legacy sync we did in Task 2) to ensure that every warehouse move is reflected in the financial ledger immediately.

Data Consistency: How do we handle currency conversions or tax variations across different regional locations?

Benefit: Real-time visibility allows for "Pivot-on-the-fly" decisions (e.g., stopping a promotion if fulfillment costs are spiking).
```
## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
```txt
Historical Baselines: The system must store at least 2 years of data to account for seasonality (e.g., Black Friday spikes).

Predictive Modeling: Designing the system to ingest external signals (weather, shipping strikes, marketing calendars) to adjust predicted labor needs.

What-If Analysis: The system should allow managers to simulate: "What happens to our budget if we replace Warehouse A with a larger Warehouse B next month?"

Resource Allocation: Budgeting isn't just about money; it's about "Capacity." Can we forecast if a Location's maxCapacity will be hit before it actually happens?
```
## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
```txt
Comparability: By reusing the Business Unit Code but archiving the old entity, we create a "Linked History." We need this to answer: "Is the new Warehouse actually more cost-effective than the one it replaced?"

Amortization: If we close an old Warehouse, how do we handle the "Sunk Costs" or remaining lease liabilities? These must stay associated with that BU Code's financial history.

Budgeting Continuity: The new Warehouse starts with a clean slate for operational stock, but its Budget is likely a continuation of the previous one. Preserving history prevents the new facility from appearing "cheaper" simply because it hasn't incurred maintenance costs yet.

Audit Compliance: Regulators require a clear trail of where inventory (and its value) went during the transition.
```
## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
