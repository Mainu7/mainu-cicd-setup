# mainu-cicd-setup
#Multi-microservice CI/CD automation using Jenkins Job DSL
# 🚀 Mainu-CICD — Multi-Microservice Jenkins Automation

[![Jenkins](https://img.shields.io/badge/Jenkins-Pipeline-red)](https://www.jenkins.io/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue)](https://www.docker.com/)
[![AWS ECR](https://img.shields.io/badge/AWS-ECR-orange)](https://aws.amazon.com/ecr/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

## 📌 Overview

A complete **CI/CD automation framework** built with Jenkins Job DSL that dynamically generates and manages build pipelines for **30+ microservices** — eliminating the need to manually create and configure each pipeline individually.

This project solves a real-world DevOps challenge: managing CI/CD for a large microservices architecture where manually creating 30+ Jenkins pipelines would take days, and any configuration change would require updating each pipeline separately.

---

## 🎯 Problem Statement

In a microservices architecture with 30+ independent services:
- ❌ Creating individual Jenkins pipelines manually was time-consuming (~30-45 mins per service)
- ❌ Any change to pipeline logic required updating 30+ jobs individually
- ❌ Inconsistent configurations across different services
- ❌ No easy way to onboard new microservices quickly
- ❌ Teams needed to build/deploy only specific services, not all at once

## ✅ Solution

Built a **Jenkins Job DSL Seed Job** that:
- Generates all microservice pipelines from a single Groovy script
- Maintains consistency across all pipeline configurations
- Allows bulk updates by modifying one central template
- Supports selective builds (choose which service to build/deploy)
- Reduces new service onboarding time from hours to minutes

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| CI/CD | Jenkins, Job DSL Plugin |
| Containerization | Docker |
| Cloud Registry | AWS ECR |
| Orchestration | AWS EKS (Kubernetes) |
| Build Tool | Maven |
| Scripting | Groovy |
| Version Control | AWS CodeCommit / Git |

---

## 🏗️ Architecture

┌─────────────────────────────────────────────────────────────────┐
│ MAINU-CICD ARCHITECTURE │
└─────────────────────────────────────────────────────────────────┘

DEVELOPER
│
│ git push
▼
┌──────────────┐
│ Git Repo │ (CodeCommit / GitHub / GitLab)
│ 30+ Service │
│ Repos │
└──────┬───────┘
│ SCM Checkout
▼
┌─────────────────────────────────────────────────────────────────┐
│ JENKINS SERVER │
│ │
│ ┌─────────────┐ runs ┌─────────────────────┐ │
│ │ Seed Job │ ────────► │ Job DSL Plugin │ │
│ │ (Groovy) │ │ Auto-generates │ │
│ └─────────────┘ │ 30+ Pipelines │ │
│ └─────────────────────┘ │
│ │
│ ┌───────────┐ ┌─────────┐ ┌─────────┐ ┌──────────┐ │
│ │ SCM │─►│ Maven │─►│ Docker │─►│ ECR │ │
│ │ Checkout │ │ Build │ │ Build │ │ Push + │ │
│ │ │ │ (Jar) │ │ & Tag │ │ Retag │ │
│ └───────────┘ └─────────┘ └─────────┘ └──────────┘ │
└─────────────────────────────────────────────────────────────────┘
│ docker push
▼
┌──────────────────────────┐
│ AWS ECR │
│ devlatest ← rolling │
│ devV1 ← release │
│ 30+ repositories │
└──────────────────────────┘
│ Manual deployment
▼
┌──────────────────────────┐
│ Kubernetes / EKS │
│ kubectl apply yaml │
└──────────────────────────┘