# H2 Database Benchmark

## Project Summary
This project provides a simple framework to benchmark the performance of an H2 database running in server mode. It specifically focuses on comparing the performance of `INSERT` and `MERGE` (upsert) operations under different scenarios, such as varying row counts and column quantities.

## Features
- Benchmarks `INSERT` vs. `MERGE` operations.
- Allows for easy configuration of the number of rows for benchmarking.
- Supports benchmarking with different table structures.
- Demonstrates how to programmatically start and stop an H2 server from within a Java application.

## How to Run

1.  **Prerequisites:**
    *   Java Development Kit (JDK) 8 or higher
    *   Apache Maven

2.  **Build the project:**
    Navigate to the `h2-benchmark` directory and run the following command to build the project and create an executable JAR:
    ```bash
    mvn package
    ```

3.  **Run the benchmark:**
    Execute the benchmark using the following command:
    ```bash
    java -jar target/h2-benchmark-1.0-SNAPSHOT.jar
    ```

## Benchmark Results

The following benchmarks were performed to compare `INSERT` and `MERGE` operations.

### Benchmark 1: 10,000 Rows (2 Columns)
-   **Table Schema:** `id INT, name VARCHAR`
-   **Operation:** Inserting 10,000 new rows into an empty table.

| Operation | Time Taken |
| :-------- | :--------- |
| `INSERT`  | 404 ms     |
| `MERGE`   | 340 ms     |

### Benchmark 2: 10,000 Rows (12 Columns)
-   **Table Schema:** `id INT, name VARCHAR, col1-10 VARCHAR`
-   **Operation:** Inserting 10,000 new rows into an empty table.

| Operation | Time Taken |
| :-------- | :--------- |
| `INSERT`  | 472 ms     |
| `MERGE`   | 387 ms     |

### Benchmark 3: 100,000 Rows on Existing Data (12 Columns)
-   **Table Schema:** `id INT, name VARCHAR, col1-10 VARCHAR`
-   **Operation:** Inserting 100,000 new rows into a table already containing 1,000,000 rows.

| Operation | Time Taken |
| :-------- | :--------- |
| `INSERT`  | 2518 ms    |
| `MERGE`   | 2568 ms    |

## Database Size

-   **After 1,000,000 rows (12 columns):** 94 MB
-   **After 1,200,000 rows (12 columns):** 116 MB
