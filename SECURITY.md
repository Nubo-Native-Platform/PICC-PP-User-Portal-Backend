# Security Policy

## Reporting a Vulnerability

Please **do not open a public issue** to report vulnerabilities. Email **contribution@nubons.com** with a detailed description, impact assessment, and reproduction steps. We aim to acknowledge reports within 5 business days.

## Zero Secrets Policy

This repository must never contain secrets, API tokens, passwords, private keys, `.env` files, or internal infrastructure IP addresses. Configuration is supplied at runtime via environment variables.

If you spot a committed secret, report it privately using the address above so it can be rotated and purged from version history.
