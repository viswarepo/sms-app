# Notification Service

Email (Mailgun) and SMS (Twilio) notifications for the subscription
management system. Java 21, Spring Boot 4.0.7, PostgreSQL.

## What it does

- **Templates** — reusable message templates per channel (EMAIL/SMS) with
  `{{placeholder}}` substitution (e.g. `{{customerName}}`, `{{amount}}`).
- **Send** — send an email or SMS either from a template + variables, or
  as an ad-hoc subject/body/message.
- **Logs** — every send attempt is recorded (`NotificationLog`) regardless
  of outcome, including the provider's message id for later correlation.
- **Webhooks** — Mailgun delivery-status webhooks and Twilio status
  callbacks both update the log's status (`SENT` → `DELIVERED`/`FAILED`/`BOUNCED`)
  asynchronously, since acceptance by the provider isn't the same as
  actual delivery.

## Setup

1. **Database** — create a Postgres database, point `spring.datasource.*`
   at it (or override via env vars).

2. **Mailgun**
   - API key: https://app.mailgun.com/app/account/security/api_keys (Private API key)
   - Domain: your verified sending domain (e.g. `mg.yourdomain.com`)
   - Webhook signing key: Settings → Webhooks in the Mailgun dashboard
   - Register `POST https://<your-host>/api/v1/webhooks/mailgun` under
     Webhooks for at least `delivered`, `failed`, `bounced` events.
   ```
   MAILGUN_API_KEY=...
   MAILGUN_DOMAIN=mg.yourdomain.com
   MAILGUN_FROM=Your App <no-reply@mg.yourdomain.com>
   MAILGUN_WEBHOOK_SIGNING_KEY=...
   ```
   If you're on Mailgun's EU region, also set
   `MAILGUN_BASE_URL=https://api.eu.mailgun.net/v3`.

3. **Twilio**
   - Account SID / Auth Token: https://console.twilio.com dashboard home
   - From number: an SMS-capable Twilio number, E.164 format
   - Register `POST https://<your-host>/api/v1/webhooks/twilio/status` as
     the Status Callback URL on outgoing messages (set this per-message via
     the SDK, or as an account-level default in the console).
   ```
   TWILIO_ACCOUNT_SID=...
   TWILIO_AUTH_TOKEN=...
   TWILIO_FROM_NUMBER=+1...
   ```

4. **Run**
   ```bash
   ./mvnw clean package -DskipTests
   java -jar target/notification-service-0.0.1-SNAPSHOT.jar
   ```
   Runs on port **9094** by default.

## Typical flow

```
1. POST /api/v1/notification-templates       create templates once, e.g.
     { "code": "payment-failed", "channel": "EMAIL",
       "subject": "Payment failed for {{planName}}",
       "body": "Hi {{customerName}}, your payment of {{amount}} failed..." }

2. POST /api/v1/notifications/email          triggered from billing-payment-service, e.g.
     { "to": "jane@example.com", "templateCode": "payment-failed",
       "variables": { "customerName": "Jane", "planName": "Pro", "amount": "₹999" } }

   -- or ad-hoc, no template --
     { "to": "jane@example.com", "subject": "...", "body": "..." }

3. Mailgun/Twilio deliver asynchronously; their webhooks hit this service
   and update the NotificationLog status automatically.
```

## Local testing without real Mailgun/Twilio webhooks reaching you

Webhooks need a publicly reachable URL. For local development, tunnel this
service with `ngrok http 9094` and register the tunnel's HTTPS URL as the
webhook endpoint in each provider's dashboard.

## Gateway routing

If exposing this behind the API gateway, note that `/api/v1/webhooks/**`
should bypass the gateway's JWT auth filter — Mailgun and Twilio aren't
sending bearer tokens, only their own signatures (verified independently
by this service). Everything else (`/api/v1/notifications/**`,
`/api/v1/notification-templates/**`) can go through the normal
authenticated route.
