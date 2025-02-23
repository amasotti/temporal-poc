# Temporal Workflow Enginer - PoC


## Introduction
_tbd_


## Installation

Besides the obvious (Kotlin, Java JDK 21, etc.), you need to [install the Temporal CLI](https://learn.temporal.io/getting_started/java/dev_environment/) for staring the workers.
On macOS, you can do this with Homebrew:

~~~sh
brew install temporal
~~~

## Project Structure

A Temporal Application (i.e. a set of workflows and activities) is structured as follows:

- `Workflows` like [MoneyTransferWorkflow](src/main/kotlin/com/tonihacks/temporalpoc/MoneyTransferWorkflow.kt) -  A Workflow defines the overall flow of the application.
- `Activities` like [MoneyTransferActivities](src/main/kotlin/com/tonihacks/temporalpoc/AccountActivityImpl.kt) - An Activity is a method that encapsulates business logic prone to failure (e.g., calling a service that may go down). These Activities can be automatically retried upon some failure.
- `Workers` like [MoneyTransferWorker](src/main/kotlin/com/tonihacks/temporalpoc/MoneyTransferWorker.kt) - A Worker is a process that hosts the Activities and Workflows. It listens for tasks from the Temporal service and executes


**Read more [here](https://learn.temporal.io/)**

_Code copied from the demo Java Repo:_ https://github.com/temporalio/money-transfer-project-java/tree/main


## Why Temporal for Bikeleasing:

- Ideal for a service landscape using Kotlin / Java as the main language
- SDK Available also for PHP, Typescript and .NET which means we can start using it in all our services, while still having the same workflow engine and although we're not yet ready to migrate entire services to Kotlin.
- Workflows as Code: better to debug, audit, test than low-code solutions
- Scalable and fault-tolerant
