# APS TOOLS Admin Panel Setup

Admin URL:
https://apstools.akhisave.online/admin/

## 1. Enable Firebase email/password sign-in

Open Firebase Console → Authentication → Sign-in method and enable **Email/Password**.

Create the private administrator account there. Do not put the password in the repository.

## 2. Add the administrator to Firestore

Open Firestore Database → Data.

Create:

`admins/{AUTH_USER_UID}`

Fields:

`active` = boolean `true`

Use the UID shown by Firebase Authentication for your administrator account.

The admin dashboard will only allow an authenticated account that also has this active admin document.

## 3. Deploy Firestore rules

Publish the repository's `firestore.rules`.

The rules allow APS TOOLS installations to create/update only the anonymous analytics records used by the dashboard. Analytics reads are restricted to authenticated active admins. Admin documents cannot be changed from the public client.

## 4. Open the dashboard

Sign in at:

https://apstools.akhisave.online/admin/

The dashboard reads:
- `analytics_users`
- `analytics_events`

The app records anonymous installation ID, app version, Android version, device model/manufacturer, app opens, tool opens and tool actions.

No names, phone numbers or email addresses are collected by this analytics mirror.

## Important

The current app source is the V2 analytics implementation. The automatic release workflow can publish a new version after the Android-source commits.

Before using this as a production admin system, enable Firebase App Check and consider server-side aggregation/rate limits for larger traffic volumes.
