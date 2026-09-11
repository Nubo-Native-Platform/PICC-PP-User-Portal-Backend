# Contributing to Nubo Native Platform (NNP)

This repository — **PICC-PP-User-Portal-Backend** — is part of the **Platform Infrastructure and Core Components (PICC)** area of the Nubo Native Platform. Contributions are welcome under the **Apache 2.0 License**.

## Before You Start

Contributions should align with an open **Issue**, the published **Roadmap**, or a proposed **enhancement**. Email **contribution@nubons.com** with your approach and category first; we respond within 5 working days.

## Development & Contribution Steps

1. **Fork & Clone**: Fork the repository and create a feature branch.
2. **Follow Development Guidelines**: Review [DEVELOPMENT_GUIDELINES.md](DEVELOPMENT_GUIDELINES.md) for coding conventions, SAST, and test standards.
3. **Automated Verification**: Ensure `./mvnw clean test` and `./mvnw spotbugs:check` pass.
4. **Submit PR**: Open a Pull Request with a descriptive summary of your changes.

## Security & Standards

- **Never commit secrets, tokens, internal hostnames, or `.env` files.** See [SECURITY.md](SECURITY.md).
- All participation is governed by our [Code of Conduct](CODE_OF_CONDUCT.md).
